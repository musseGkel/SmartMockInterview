<script setup lang="ts">
const route = useRoute();
const { getHistorySession } = useInterview();

const sessionId = computed(() => {
  const value = route.params.sessionId;
  return typeof value === "string" ? value : "";
});

const loading = ref(true);
const error = ref<string | null>(null);
const session = ref<InterviewHistoryDetailResponse | null>(null);

try {
  session.value = await getHistorySession(sessionId.value);
} catch {
  error.value = "Failed to load interview session";
} finally {
  loading.value = false;
}
</script>

<template>
  <div class="min-h-screen bg-gray-950 text-white px-6 py-10">
    <div class="max-w-4xl mx-auto space-y-6">
      <NuxtLink to="/history" class="text-blue-400 hover:text-blue-300">
        Back to history
      </NuxtLink>

      <div v-if="loading" class="text-blue-400">Loading...</div>
      <div v-else-if="error" class="text-red-400">{{ error }}</div>

      <div v-else-if="session" class="space-y-6">
        <div class="bg-gray-900 border border-gray-800 rounded-xl p-5">
          <h1 class="text-2xl font-bold">{{ session.domain }}</h1>
          <p class="text-sm text-gray-400 mt-2">State: {{ session.state }}</p>
          <p class="text-sm text-gray-400">
            Current question: {{ session.currentQuestion }}
          </p>
        </div>

        <div class="space-y-4">
          <div
            v-for="turn in session.history"
            :key="turn.turnNumber"
            class="bg-gray-900 border border-gray-800 rounded-xl p-5 space-y-3"
          >
            <div class="font-semibold">Turn {{ turn.turnNumber }}</div>
            <div>
              <div class="text-sm text-gray-400">Question</div>
              <div>{{ turn.question }}</div>
            </div>
            <div>
              <div class="text-sm text-gray-400">Answer</div>
              <div>{{ turn.answer }}</div>
            </div>
            <div v-if="turn.feedback">
              <div class="text-sm text-gray-400">Feedback</div>
              <div>Score: {{ turn.feedback.score }}/5</div>
              <div class="mt-2 text-sm text-gray-300">
                {{ turn.feedback.summary }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
