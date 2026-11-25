<template>
  <div
    class="flex flex-col"
    :style="{color: 'black', background: 'pink', 'border-radius': '10px'}"
  >
    <div v-for="(item, index) in todos">
      <!-- Recursive rendering of subtask -->
      <TodoNode 
        :key="item.id"
        :todo="item"
        :index="index"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import TodoNode from "./TodoNode.vue";

const {todos} = defineProps(['todos']);

</script>

<style scoped>
</style>