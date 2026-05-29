<template>
  <div class="card flex flex-col justify-center m-3" v-bind:style="{background: '#c084fc', color: 'black'}">
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