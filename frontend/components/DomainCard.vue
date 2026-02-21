<script setup lang="ts">
defineProps<{
  domain: string
}>()

defineEmits<{
  (e: 'select', domain: string): void
}>()
</script>

<template>
  <div
    @click="$emit('select', domain)"
    class="bg-gray-800 hover:bg-gray-700 cursor-pointer rounded-xl p-6 transition-all border border-gray-700 hover:border-blue-500"
  >
    <h2 class="text-xl font-semibold">
      {{ domain }}
    </h2>
  </div>
</template>
