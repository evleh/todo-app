<template>
  <div
    class="flex flex-col"
    :style="{color: 'black', background: 'pink', 'border-radius': '10px'}"
  >
    <div
      v-for="(item, index) in todos"
      :key="item.id"
    >
      <!-- Recursive rendering of subtask -->
      <TodoNode 
        :todo="item"
        :index="index"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import TodoNode from "./TodoNode.vue";
import type { TodoResponse } from "../models/TodoResponse.ts";

const {todos} = defineProps<{todos:Array<TodoResponse>}>();

</script>

<style scoped>
</style>