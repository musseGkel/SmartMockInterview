<script setup lang="ts">
defineProps<{
  feedback: Feedback;
}>();
</script>

<template>
  <div class="bg-gray-800 p-6 rounded-xl border border-gray-700 space-y-4">
    <div class="text-lg font-semibold">Score: {{ feedback.score }}/5</div>

    <div>
      <h3 class="font-semibold text-green-400">Strengths</h3>
      <ul class="list-disc ml-6 space-y-1">
        <li v-for="s in feedback.strengths" :key="s">
          {{ s }}
        </li>
      </ul>
    </div>

    <div>
      <h3 class="font-semibold text-yellow-400">Improvements</h3>
      <ul class="list-disc ml-6 space-y-1">
        <li v-for="i in feedback.improvements" :key="i">
          {{ i }}
        </li>
      </ul>
    </div>

    <div class="text-gray-300">
      {{ feedback.summary }}
    </div>
  </div>
</template>
