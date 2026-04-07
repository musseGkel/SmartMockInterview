import { defineStore } from "pinia";

interface MeResponse {
  userId: string;
  email: string;
}

interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  userId: string;
  email: string;
}

interface RefreshResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

export const useAuthStore = defineStore("auth", {
  state: () => ({
    accessToken: null as string | null,
    user: null as MeResponse | null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.accessToken,
  },

  actions: {
    setAccessToken(token: string | null) {
      this.accessToken = token;
    },

    setUser(user: MeResponse | null) {
      this.user = user;
    },

    clearAuth() {
      this.accessToken = null;
      this.user = null;
    },

    async login(email: string, password: string) {
      const config = useRuntimeConfig();

      const response = await $fetch<AuthResponse>("/auth/login", {
        baseURL: config.public.apiBase,
        method: "POST",
        body: { email, password },
      });

      this.accessToken = response.accessToken;
      this.user = {
        userId: response.userId,
        email: response.email,
      };
    },

    async register(email: string, password: string) {
      const config = useRuntimeConfig();

      const response = await $fetch<AuthResponse>("/auth/register", {
        baseURL: config.public.apiBase,
        method: "POST",
        body: { email, password },
      });

      this.accessToken = response.accessToken;
      this.user = {
        userId: response.userId,
        email: response.email,
      };
    },

    async refreshAccessToken() {
      const config = useRuntimeConfig();

      const response = await $fetch<RefreshResponse>("/auth/refresh", {
        baseURL: config.public.apiBase,
        method: "POST",
        credentials: "include",
      });

      this.accessToken = response.accessToken;
      return response.accessToken;
    },

    async fetchMe() {
      if (!this.accessToken) return null;

      const apiFetch = useApiFetch();

      const me = await apiFetch<MeResponse>("/api/me", {
        method: "GET",
      });

      this.user = me;
      return me;
    },

    async logout() {
      const config = useRuntimeConfig();

      try {
        await $fetch("/auth/logout", {
          baseURL: config.public.apiBase,
          method: "POST",
        });
      } finally {
        this.clearAuth();
      }
    },
  },
});
