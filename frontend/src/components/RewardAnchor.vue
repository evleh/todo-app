<script setup lang="ts">
import AnimationBackground from './RewardBackground.vue/index.js';
import type { AnimationMeta } from '../rewards/index.ts';

defineProps<{
  show: boolean;
  animation: AnimationMeta | null;
}>();

const emit = defineEmits<{ complete: [] }>();
</script>

<template>
  <div class="reward-anchor">
    <AnimationBackground :active="show" :animation="animation" @complete="emit('complete')" />
    <div class="reward-anchor-content"><slot /></div>
  </div>
</template>

<style scoped>
.reward-anchor {
  position: relative;
  overflow: hidden;
}
.reward-anchor-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
}
</style>
