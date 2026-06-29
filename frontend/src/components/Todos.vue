<template>
  <div class="card flex flex-col m-2 p-5" v-bind:style="{background: '#1112', color: 'black'}">
    <div v-for="(item, index) in todos">
      <Todo :todo="item"
            :index="index"
            :key="item.id"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import Todo from "./Todo.vue";
import {defineProps} from 'vue';

const {todos} = defineProps(['todos']);

</script>

<style scoped>
</style>