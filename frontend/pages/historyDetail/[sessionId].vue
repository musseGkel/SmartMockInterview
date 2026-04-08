<script setup lang="ts">
const route = useRoute();
const { getHistorySession } = useInterview();

definePageMeta({
  middleware: "auth",
});

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

const openTurnNumber = ref<number | null>(null);

const toggleTurn = (turnNumber: number) => {
  openTurnNumber.value =
    openTurnNumber.value === turnNumber ? null : turnNumber;
};
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
        </div>

        <div class="space-y-4">
          <TranscriptItem
            v-for="turn in session.history"
            :key="turn.turnNumber"
            :turn="turn"
            :index="turn.turnNumber"
            :open="openTurnNumber === turn.turnNumber"
            @toggle="turn.turnNumber != null && toggleTurn(turn.turnNumber)"
          />
        </div>
      </div>
    </div>
  </div>
</template>
