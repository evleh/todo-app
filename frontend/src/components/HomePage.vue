<template>
  <NewTodo @todo-created="loadTodos"></NewTodo>

  <Todos :todos="openTodos"></Todos>

  <Todos :todos="doneTodos"></Todos>

  <Button @click="logout">Logout</Button>
</template>

<script setup lang="ts">
import {onBeforeMount} from "@vue/runtime-core";
import {ref, provide, computed} from "vue";
import type {TodoResponse} from "../models/TodoResponse.ts";
import {TodoService} from "../services/TodoService.ts";
import router from "../router.ts";
import {AuthService} from "../services/AuthService.ts";
import Todos from "./Todos.vue";
import Button from 'primevue/button';
import NewTodo from "./NewTodo.vue";

let todos = ref<Array<TodoResponse>>([]);

const openTodos = computed(() => todos.value.filter(todo => !todo.done));
const doneTodos = computed(() => todos.value.filter(todo => todo.done));


const loadTodos = async () => {
  todos.value = await TodoService.readAll();
};

provide(/* key */ 'loadTodos', /* value */ loadTodos);

onBeforeMount(loadTodos);

function logout(){
  AuthService.logout();
  router.push("/");
}
</script>

<style scoped>

</style>