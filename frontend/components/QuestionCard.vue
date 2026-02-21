<script setup lang="ts">
defineProps<{
  question: string
  topic?: string
}>()
</script>

<template>
  <div class="space-y-3">
    <div
      v-if="topic"
      class="text-blue-400 text-sm uppercase tracking-wider"
    >
      {{ topic }}
    </div>

    <div class="bg-gray-800 p-6 rounded-xl border border-gray-700">
      <h2 class="text-xl font-semibold leading-relaxed">
        {{ question }}
      </h2>
    </div>
  </div>
</template>
