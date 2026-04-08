<script setup lang="ts">
const { getAnalytics } = useInterview();

definePageMeta({
  middleware: "auth",
});

const loading = ref(true);
const error = ref<string | null>(null);
const analytics = ref<InterviewAnalyticsResponse | null>(null);

try {
  analytics.value = await getAnalytics();
} catch {
  error.value = "Failed to load analytics";
} finally {
  loading.value = false;
}

const startNew = async () => {
  await navigateTo("/");
};
</script>

<template>
  <div class="min-h-screen bg-gray-950 text-white px-6 py-10">
    <div class="max-w-3xl mx-auto space-y-6">
      <div class="flex justify-between items-center">
        <h1 class="text-2xl font-bold">Analytics</h1>
        <button
          @click="startNew"
          class="text-sm text-gray-400 hover:text-white transition"
        >
          Exit
        </button>
      </div>
      <div v-if="loading" class="text-blue-400">Loading...</div>
      <div v-else-if="error" class="text-red-400">{{ error }}</div>

      <div v-else-if="analytics" class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-gray-900 border border-gray-800 rounded-xl p-5">
          <div class="text-sm text-gray-400">Total sessions</div>
          <div class="text-2xl font-bold mt-2">
            {{ analytics.totalSessions }}
          </div>
        </div>
        <div class="bg-gray-900 border border-gray-800 rounded-xl p-5">
          <div class="text-sm text-gray-400">Active sessions</div>
          <div class="text-2xl font-bold mt-2">
            {{ analytics.activeSessions }}
          </div>
        </div>
        <div class="bg-gray-900 border border-gray-800 rounded-xl p-5">
          <div class="text-sm text-gray-400">Finished sessions</div>
          <div class="text-2xl font-bold mt-2">
            {{ analytics.finishedSessions }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
