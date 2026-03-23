<template>
  <div class="card flex justify-center">
    <Form v-slot="$form" :resolver="resolver" :initialValues="initialValues" @submit="onFormSubmit" class="flex flex-col gap-4 w-full sm:w-56">
      <div class="flex flex-col gap-1">
        <InputText name="username" type="text" placeholder="Username" fluid />
        <Message v-if="$form.username?.invalid" severity="error" size="small" variant="simple">{{ $form.username.error?.message }}</Message>
      </div>
      <div class="flex flex-col gap-1">
        <InputText name="password" type="password" placeholder="Password" fluid />
        <Message v-if="$form.password?.invalid" severity="error" size="small" variant="simple">{{ $form.password.error?.message }}</Message>
      </div>
      <Button type="submit" severity="secondary" label="Submit" />
    </Form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { Form } from '@primevue/forms';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import Message from 'primevue/message';
import { zodResolver } from '@primevue/forms/resolvers/zod';
import { useToast } from "primevue/usetoast";
import {UserService} from "../../services/UserService.ts";
import { z } from 'zod';

const toast = useToast();
const initialValues = ref({
  username: '',
  email: ''
});

const resolver = ref(zodResolver(
    z.object({
      username: z.string().min(1, { message: 'Username is required.' }),
      password: z.string().min(1, { message: 'Password is required.' })
    })
));

const onFormSubmit = async ({ valid, values }) => {
  if (valid) {
    try {
      await UserService.createUser(values.username, values.password);
      toast.add({ severity: 'success', summary: 'Registration was successful.', life: 3000 });
    } catch (e) {
      toast.add({ severity: 'error', summary: 'Registration was not successful.', life: 3000 });
    }
  }
};
</script>

<style scoped>

</style>