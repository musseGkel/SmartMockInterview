export default defineNuxtRouteMiddleware(async () => {
  const auth = useAuthStore();

  if (auth.isAuthenticated) return;

  try {
    await auth.refreshAccessToken();
    await auth.fetchMe();
  } catch {
    return navigateTo('/login');
  }
});