<script setup lang="ts">
const auth = useAuthStore();
const email = ref("");
const password = ref("");
const loading = ref(false);
const error = ref<string | null>(null);

const submit = async () => {
  loading.value = true;
  error.value = null;

  try {
    await auth.register(email.value, password.value);
    await navigateTo("/");
  } catch {
    error.value = "Registration failed";
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div
    class="min-h-screen bg-gray-950 text-white flex items-center justify-center px-6"
  >
    <div
      class="w-full max-w-md bg-gray-900 border border-gray-800 rounded-xl p-6 space-y-4"
    >
      <h1 class="text-2xl font-bold">Register</h1>

      <div v-if="error" class="text-red-400">{{ error }}</div>

      <input
        v-model="email"
        type="email"
        placeholder="Email"
        class="w-full bg-gray-800 border border-gray-700 rounded-lg p-3"
      />

      <input
        v-model="password"
        type="password"
        placeholder="Password"
        class="w-full bg-gray-800 border border-gray-700 rounded-lg p-3"
      />

      <button
        @click="submit"
        :disabled="loading"
        class="w-full bg-blue-600 hover:bg-blue-500 disabled:opacity-50 rounded-lg py-3 font-semibold"
      >
        {{ loading ? "Registering..." : "Register" }}
      </button>
    </div>
  </div>
</template>
