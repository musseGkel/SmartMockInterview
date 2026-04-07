export function useApiFetch() {
  const config = useRuntimeConfig();
  const auth = useAuthStore();

  return async function apiFetch<T>(
    url: string,
    options: Record<string, any> = {},
  ): Promise<T> {
    const headers: Record<string, string> = {
      ...(options.headers || {}),
    };

    if (auth.accessToken) {
      headers.Authorization = `Bearer ${auth.accessToken}`;
    }

    try {
      return await $fetch<T>(url, {
        baseURL: config.public.apiBase,
        credentials: "include",
        ...options,
        headers,
      });
    } catch (error: any) {
      const status = error?.response?.status;

      if (status !== 401 || options._retried) {
        throw error;
      }

      try {
        const refreshResponse = await $fetch<{ accessToken: string }>(
          "/auth/refresh",
          {
            baseURL: config.public.apiBase,
            method: "POST",
            credentials: "include",
          },
        );

        auth.setAccessToken(refreshResponse.accessToken);

        return await $fetch<T>(url, {
          baseURL: config.public.apiBase,
          credentials: "include",
          ...options,
          headers: {
            ...(options.headers || {}),
            Authorization: `Bearer ${refreshResponse.accessToken}`,
          },
          _retried: true,
        } as any);
      } catch (refreshError) {
        auth.clearAuth();
        throw refreshError;
      }
    }
  };
}