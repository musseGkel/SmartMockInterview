<script setup lang="ts">
interface StartResponse {
  sessionId: string;
  question: string;
  topic?: string;
}

const auth = useAuthStore();

const logout = async () => {
  await auth.logout();
};

const { getDomains, startInterview } = useInterview();

const loading = ref(false);
const error = ref<string | null>(null);

const { data: domains, error: loadError } = await useAsyncData("domains", () =>
  getDomains(),
);

if (loadError.value) {
  error.value = "Failed to load domains";
}

const begin = async (domain: string) => {
  loading.value = true;
  error.value = null;

  try {
    const res = (await startInterview(domain)) as StartResponse;

    await navigateTo({
      path: "/interview",
      query: {
        sessionId: res.sessionId,
        question: res.question,
        topic: res.topic ?? "",
        domain,
      },
    });
  } catch (e) {
    error.value = "Failed to start interview";
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="absolute top-6 right-6 flex items-center gap-4">
    <template v-if="auth.isAuthenticated">
      <NuxtLink to="/history" class="text-sm text-gray-300 hover:text-white">
        History
      </NuxtLink>

      <NuxtLink to="/analytics" class="text-sm text-gray-300 hover:text-white">
        Analytics
      </NuxtLink>

      <span class="text-sm text-gray-400">
        {{ auth.user?.email }}
      </span>

      <button
        @click="logout"
        class="text-sm bg-gray-800 hover:bg-gray-700 px-3 py-1 rounded"
      >
        Logout
      </button>
    </template>

    <template v-else>
      <NuxtLink to="/login" class="text-sm text-gray-300 hover:text-white">
        Login
      </NuxtLink>

      <NuxtLink
        to="/register"
        class="text-sm bg-blue-600 hover:bg-blue-500 px-3 py-1 rounded"
      >
        Register
      </NuxtLink>
    </template>
  </div>
  <div
    class="min-h-screen bg-gray-950 text-white flex flex-col items-center justify-center px-6"
  >
    <div class="text-center mb-12">
      <h1 class="text-4xl md:text-5xl font-bold mb-4">AI Interview Practice</h1>
      <p class="text-gray-400">Select a domain and begin your mock interview</p>
    </div>

    <div v-if="error" class="text-red-400 mb-6">
      {{ error }}
    </div>

    <div v-if="loading" class="flex items-center gap-2 text-blue-400 mb-6">
      <CircularIndicator />
      <span>Starting interview...</span>
    </div>
    <div
      v-if="domains"
      class="grid grid-cols-1 md:grid-cols-2 gap-6 w-full max-w-3xl"
    >
      <DomainCard
        v-for="domain in domains"
        :key="domain"
        :domain="domain"
        @select="begin"
      />
    </div>
  </div>
</template>
