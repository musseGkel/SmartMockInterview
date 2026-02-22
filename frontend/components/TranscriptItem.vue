<script setup lang="ts">
defineProps<{
  turn: QuestionTurn;
  index: number;
  open: boolean;
}>();

defineEmits<{
  (e: "toggle"): void;
}>();
</script>

<template>
  <div class="space-y-3">
    <div class="bg-gray-900 border border-gray-800 rounded-xl p-5">
      <div class="flex items-start justify-between gap-4">
        <div class="min-w-0">
          <div class="font-semibold text-white">
            Q{{ turn.turnNumber }}: {{ turn.question }}
          </div>

          <div class="text-sm text-gray-400 mt-2">
            Score: {{ turn.feedback?.score ?? "-" }}/5
          </div>
        </div>

        <button
          @click="$emit('toggle')"
          class="shrink-0 text-sm px-3 py-1 rounded-lg border border-gray-700 hover:border-gray-500 text-gray-300 hover:text-white transition"
        >
          {{ open ? "Hide" : "Show" }}
        </button>
      </div>

      <div v-if="open" class="mt-4 space-y-4">
        <div class="text-gray-200 whitespace-pre-wrap">
          <div class="font-semibold text-gray-300 mb-1">Your answer</div>
          <div>{{ turn.answer }}</div>
        </div>
      </div>
    </div>

    <div v-if="open">
      <FeedbackCard :feedback="turn.feedback" />
    </div>
  </div>
</template>
