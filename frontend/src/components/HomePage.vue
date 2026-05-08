<template>
  <div class="card flex justify-center">
    <Button @click="logout">Logout</Button>
  </div>
</template>

<script setup lang="ts">
import router from "../router.ts";
import {AuthService} from "../services/AuthService.ts";

function logout(){
  AuthService.logout();
  router.push("/");
}
</script>

<style scoped>

</style>