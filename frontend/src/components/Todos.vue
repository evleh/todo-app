<template>
  <div class="flex flex-col" v-bind:style="{color: 'black', background: 'pink', 'border-radius': '10px'}">
    <div v-for="(item, index) in todos">
      <!-- Recursive rendering of subtask -->
      <TodoNode 
        :todo="item"
        :index="index"
        :key="item.id"
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