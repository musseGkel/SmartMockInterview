<script setup lang="ts">
const { getHistory } = useInterview();

definePageMeta({
  middleware: "auth",
});

const loading = ref(true);
const error = ref<string | null>(null);
const historyItems = ref<InterviewHistoryItemResponse[]>([]);

try {
  historyItems.value = await getHistory();
} catch {
  error.value = "Failed to load history";
} finally {
  loading.value = false;
}

const startNew = async () => {
  await navigateTo("/");
};

const goToHistoryDetail = async (sessionId: string) => {
  await navigateTo(`/historyDetail/${sessionId}`);
};
</script>

<template>
  <div class="min-h-screen bg-gray-950 text-white px-6 py-10">
    <div class="max-w-4xl mx-auto space-y-6">
      <div class="flex justify-between items-center">
        <h1 class="text-2xl font-bold">Interview History</h1>
        <button
          @click="startNew"
          class="text-sm text-gray-400 hover:text-white transition"
        >
          Exit
        </button>
      </div>
      <div v-if="loading" class="text-blue-400">Loading...</div>
      <div v-else-if="error" class="text-red-400">{{ error }}</div>
      <div v-else-if="historyItems.length === 0" class="text-gray-400">
        No saved interviews yet.
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="item in historyItems"
          :key="item.sessionId"
          class="bg-gray-900 border border-gray-800 rounded-xl p-5"
        >
          <div class="flex items-center justify-between gap-4">
            <div>
              <div class="text-lg font-semibold">{{ item.domain }}</div>
              <div class="text-sm text-gray-400">State: {{ item.state }}</div>
            </div>

            <button
              class="text-blue-400 hover:text-blue-300"
              @click="goToHistoryDetail(item.sessionId)"
            >
              View
            </button>
          </div>

          <div class="mt-3 text-sm text-gray-300">
            Current question: {{ item.currentQuestion }}
          </div>

          <div class="mt-2 text-sm text-gray-400">
            Turns: {{ item.historyCount }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
