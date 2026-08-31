<script setup lang="ts">
import type { TodoResponse } from "../models/TodoResponse.ts";
import Todo from "./Todo.vue";
import {ref, computed} from 'vue';
import Button from 'primevue/button';


const props = defineProps<{
  todo: TodoResponse;
}>();

// expand subtasks
const isExpanded = ref(false);

// done subtasks sink to the bottom
const sortedSubtasks = computed(() =>
  [...props.todo.subtasks].sort((a, b) => Number(a.done) - Number(b.done))
);
</script>

<template>
  
  <div class="flex">
    <!-- expand / collaps subtask, with a connector line directly beneath it when expanded -->
    <div class="flex flex-col items-center">
      <Button
        v-if="todo.parentId === null"
        :disabled="todo.subtasks.length == 0"
        :icon="isExpanded ? 'pi pi-chevron-up' : 'pi pi-chevron-down'"
        text
        severity="secondary"
        aria-label="Toggle Subtasks"
        class="mt-2"
        @click="isExpanded = !isExpanded"
      />
      <div v-if="todo.subtasks.length > 0 && isExpanded" class="w-px flex-1 my-1 bg-[var(--p-button-text-secondary-color)]"></div>
    </div>

    <div class="flex flex-col flex-1 min-w-0">
      <Todo :todo="todo" :is-expanded="isExpanded" @expand-subtasks="isExpanded = true" />

      <div v-show="todo.subtasks.length > 0 && isExpanded" class="pl-7 flex flex-col">
        <TodoNode
          v-for="subtask in sortedSubtasks"
          :todo="subtask"
          :key="subtask.id"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>