<template>
  <NewTodo @todo-created="loadTodos"></NewTodo>

  <Button @click="logout">Logout</Button>
</template>

<script setup lang="ts">
import {onBeforeMount} from "@vue/runtime-core";
import {ref} from "vue";
import type {TodoResponse} from "../models/TodoResponse.ts";
import {TodoService} from "../services/TodoService.ts";
import router from "../router.ts";
import {AuthService} from "../services/AuthService.ts";

import Button from 'primevue/button';
import NewTodo from "./NewTodo.vue";

let todos = ref<Array<TodoResponse>>([]);

const loadTodos = async () => {
  todos.value = await TodoService.readAll();
};

onBeforeMount(loadTodos);

function logout(){
  AuthService.logout();
  router.push("/");
}
</script>

<style scoped>

</style>