<script setup lang="ts">
const route = useRoute();
const { submitAnswer } = useInterview();

const getQueryString = (key: string) => {
  const v = route.query[key];
  return typeof v === "string" ? v : "";
};

const sessionId = computed(() => getQueryString("sessionId"));
const domain = computed(() => getQueryString("domain"));
const question = ref(getQueryString("question"));
const topic = ref(getQueryString("topic"));

if (process.client && !sessionId.value) {
  await navigateTo("/");
}

const history = ref<QuestionTurn[]>([]);
const answer = ref("");
const loading = ref(false);
const error = ref<string | null>(null);
const finished = ref(false);

const transcriptTop = ref<HTMLElement | null>(null);

const averageScore = computed(() => {
  if (history.value.length === 0) return null;
  const sum = history.value.reduce(
    (acc, t) => acc + (t.feedback?.score ?? 0),
    0,
  );
  return Math.round((sum / history.value.length) * 10) / 10;
});

const scrollToTranscript = async () => {
  await nextTick();
  transcriptTop.value?.scrollIntoView({ behavior: "smooth", block: "start" });
};

const sendAnswer = async () => {
  if (!answer.value.trim()) return;

  loading.value = true;
  error.value = null;

  try {
    const res = (await submitAnswer(
      sessionId.value,
      answer.value,
    )) as AnswerResponse;

    history.value = res.history ?? [];
    finished.value = res.finished;

    if (!res.finished) {
      question.value = res.nextQuestion ?? "";
      topic.value = res.topic ?? "";
    } else {
      question.value = "";
      topic.value = "";
    }

    answer.value = "";
    await scrollToTranscript();
  } catch {
    error.value = "Failed to submit answer";
  } finally {
    loading.value = false;
  }
};

const orderedHistory = computed(() => {
  return [...history.value].sort(
    (a, b) => (b.turnNumber ?? 0) - (a.turnNumber ?? 0),
  );
});

const openTurnNumber = ref<number | null>(null);

const latestOpenTurn = computed(() => {
  if (history.value.length === 0) return null;
  return Math.max(...history.value.map((t) => t.turnNumber ?? 0));
});

watch(
  latestOpenTurn,
  (newVal) => {
    if (newVal !== null) openTurnNumber.value = newVal;
  },
  { immediate: true },
);

const toggleTurn = (turnNumber: number) => {
  openTurnNumber.value =
    openTurnNumber.value === turnNumber ? null : turnNumber;
};

const startNew = async () => {
  await navigateTo("/");
};
</script>

<template>
  <div
    class="min-h-screen bg-gray-950 text-white flex justify-center px-6 py-10"
  >
    <div class="w-full max-w-4xl space-y-8">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold">Interview Session</h1>
          <p v-if="domain" class="text-sm text-blue-400 mt-1">
            Domain: <span class="font-medium text-white">{{ domain }}</span>
          </p>
        </div>
        <button
          @click="startNew"
          class="text-sm text-gray-400 hover:text-white transition"
        >
          Exit
        </button>
      </div>

      <!-- Summary -->
      <div
        class="bg-gray-900 border border-gray-800 rounded-xl p-5 flex items-center justify-between gap-4"
      >
        <div class="space-y-1">
          <div class="text-sm text-gray-400">Answered</div>
          <div class="text-xl font-semibold">{{ history.length }}</div>
        </div>

        <div class="space-y-1 text-right">
          <div class="text-sm text-gray-400">Average score</div>
          <div class="text-xl font-semibold">
            <span v-if="averageScore !== null">{{ averageScore }}/5</span>
            <span v-else class="text-gray-500">-</span>
          </div>
        </div>
      </div>

      <!-- Current question -->
      <div v-if="!finished">
        <QuestionCard :question="question" :topic="topic" />

        <div class="space-y-4 mt-6">
          <textarea
            v-model="answer"
            rows="6"
            placeholder="Type your answer here..."
            class="w-full bg-gray-900 border border-gray-700 rounded-lg p-4 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          <button
            @click="sendAnswer"
            :disabled="loading || answer.trim() === ''"
            class="bg-blue-600 hover:bg-blue-500 disabled:opacity-50 px-6 py-3 rounded-lg font-semibold transition"
          >
            Submit Answer
          </button>

          <div v-if="loading" class="flex items-center gap-2 text-blue-400">
            <CircularIndicator />
            <span>Evaluating your answer...</span>
          </div>

          <div v-if="error" class="text-red-400">
            {{ error }}
          </div>
        </div>
      </div>

      <div v-else class="text-center space-y-4">
        <h2 class="text-xl font-semibold">Session Complete</h2>
        <button
          @click="startNew"
          class="bg-green-600 hover:bg-green-500 px-6 py-3 rounded-lg font-semibold"
        >
          Start New Interview
        </button>
      </div>

      <!-- Transcript -->
      <div class="pt-6 border-t border-gray-800 space-y-6">
        <div ref="transcriptTop"></div>

        <div class="flex items-center justify-between">
          <h2 class="text-lg font-semibold">Transcript</h2>
          <div class="text-sm text-gray-400">Latest opened</div>
        </div>

        <div v-if="orderedHistory.length === 0" class="text-gray-400">
          Your previous questions, answers, and feedback will appear here after
          you submit.
        </div>

        <div v-else class="space-y-6">
          <TranscriptItem
            v-for="turn in orderedHistory"
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
