export default defineNuxtPlugin(async () => {
  const auth = useAuthStore();

  try {
    await auth.refreshAccessToken();
    await auth.fetchMe();
  } catch {
    auth.clearAuth();
  }
});
