<script setup lang="ts">
import AnimationBackground from './AnimationBackground.vue';
import type { AnimationMeta } from '../animations';

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
