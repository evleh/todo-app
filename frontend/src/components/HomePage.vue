<template >
  <div class="flex flex-col" style="background: #A809A845">
    <Button style="align-self: flex-end" class="m-3" @click="logout">Logout</Button>
    <h1>Todo App</h1>
  </div>

  <div>
    <NewTodo class="mb-3" @todo-created="loadTodos"></NewTodo>
  </div>

  <Accordion v-model:value="openPanels" multiple >
    <AccordionPanel value="0" :disabled="openTodos.length === 0">
      <AccordionHeader>
        <span class="task-header"> Open Tasks:
          <span> &nbsp; {{openTodos.length}} </span>
        </span>
      </AccordionHeader>
      <AccordionContent>
        <Todos :todos="openTodos" class="tasks"></Todos>
      </AccordionContent>
    </AccordionPanel>
    <AccordionPanel value="1" :disabled="doneTodos.length === 0">
      <AccordionHeader>
        <span class="task-header"> Completed Tasks:
          <span> &nbsp; {{doneTodos.length}} </span>
        </span>
      </AccordionHeader>
      <AccordionContent>
        <Todos :todos="doneTodos" class="tasks"></Todos>
      </AccordionContent>
    </AccordionPanel>
  </Accordion>
</template>

<script setup lang="ts">
import {onBeforeMount} from "@vue/runtime-core";
import {ref, provide, computed, watch} from "vue";
import type {TodoResponse} from "../models/TodoResponse.ts";
import {TodoService} from "../services/TodoService.ts";
import router from "../router.ts";
import {AuthService} from "../services/AuthService.ts";
import Todos from "./Todos.vue";
import Button from 'primevue/button';
import NewTodo from "./NewTodo.vue";
import Accordion from 'primevue/accordion';
import AccordionPanel from 'primevue/accordionpanel';
import AccordionHeader from 'primevue/accordionheader';
import AccordionContent from 'primevue/accordioncontent';

let todos = ref<Array<TodoResponse>>([]);

const openTodos = computed(() => todos.value.filter(todo => !todo.done));
const doneTodos = computed(() => todos.value.filter(todo => todo.done));

const openPanels = ref(['0']);

watch(openTodos, (val) => {
  if (val.length === 0) openPanels.value = openPanels.value.filter(v => v !== '0');
  if (val.length !== 0) openPanels.value.push('0');
});
watch(doneTodos, (val) => {
  if (val.length === 0) openPanels.value = openPanels.value.filter(v => v !== '1');
});


const loadTodos = async () => {
  todos.value = await TodoService.readAll();
};

provide('loadTodos', loadTodos);

onBeforeMount(loadTodos);

function logout(){
  AuthService.logout();
  router.push("/");
}
</script>

<style scoped>

.tasks{
  max-height: 40vh;
  overflow-y: auto;
  padding: 1rem;
}

.task-header {
  justify-content: flex-start;
}

</style>