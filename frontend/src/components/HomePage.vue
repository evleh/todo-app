<template >
  <RewardAnchor
    class="header"
    :show="showInHeader"
    :animation="currentAnimation"
    @complete="onAnimationComplete"
    style="background-color: #A809A845;"
  >
    <Button style="align-self: flex-end" class="m-3" @click="logout">Logout</Button>
    <h1>Todo App</h1>
  </RewardAnchor>

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
import RewardAnchor from './RewardAnchor.vue';
import { randomAnimation } from '../animations';
import type { AnimationMeta } from '../animations';

// todos: data
let todos = ref<Array<TodoResponse>>([]);

const openTodos = computed(() => todos.value.filter(todo => !todo.done));
const doneTodos = computed(() => todos.value.filter(todo => todo.done));

const loadTodos = async () => {
  todos.value = await TodoService.readAll();
};

provide('loadTodos', loadTodos); // expose method to child components 

// setup
onBeforeMount(loadTodos);

function logout(){
  AuthService.logout();
  router.push("/");
}

// Accordion
const openPanels = ref(['0']);
watch(openTodos, (val) => {
  if (val.length === 0) openPanels.value = openPanels.value.filter(v => v !== '0');
  if (val.length !== 0) openPanels.value.push('0');
});
watch(doneTodos, (val) => {
  if (val.length === 0) openPanels.value = openPanels.value.filter(v => v !== '1');
});


// animation
const currentAnimation = ref<AnimationMeta | null>(null);
const isShowingAnimation = ref(false);

const showReward = () => {
  currentAnimation.value = randomAnimation();
  isShowingAnimation.value = true;
};
provide('showReward', showReward);

const showInHeader = computed(() => isShowingAnimation.value && currentAnimation.value?.region === 'header');

function onAnimationComplete() {
  isShowingAnimation.value = false;
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