<script setup lang="ts">
import RewardPlayer from './RewardPlayer.vue';
import type { RewardMeta } from '../rewards/rewards.ts';

defineProps<{
  show: boolean;
  reward: RewardMeta | null;
}>();

const emit = defineEmits<{ complete: [] }>();
</script>

<template>
  <div class="reward-anchor">
    <RewardPlayer :active="show" :animation="reward" @complete="emit('complete')" />
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
