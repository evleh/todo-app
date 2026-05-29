<script setup lang="ts">
import InputText from 'primevue/inputtext';
import {Form} from "@primevue/forms";
import Checkbox from 'primevue/checkbox';
import Button from 'primevue/button';
import {ref, inject} from 'vue';
import {TodoService} from "../services/TodoService.ts";
import DatePicker from 'primevue/datepicker';
import Message from "primevue/message";

const props = defineProps(['todo']);
const initialValues = ref({ task: props.todo.task, done: props.todo.done });
const dueDate = ref<Date | null>(parseDue(props.todo.due));
const loadTodos = inject<() => Promise<void>>('loadTodos');

function parseDue(due: unknown): Date | null {
  if (!due) return null;
  const [y, m, d] = String(due).split('T')[0].split('-').map(Number);
  return new Date(y, m - 1, d);
}

function formatDue(date: Date | null): string | null {
  if (!date) return null;
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

const toggleDone = async () => {
  try{
    await TodoService.update({id: props.todo.id, task: props.todo.task, due: props.todo.due, done: !props.todo.done});
    loadTodos();
  } catch(e){
    console.log(e);
  }
}

const onFormSubmit = async ({valid, values}: {valid: boolean; values: Record<string, unknown>}) => {
  console.log("hiiii")
  if(valid){
    try{
      await TodoService.update({id: props.todo.id, task: values.task as string, due: formatDue(dueDate.value), done: values.done as boolean});
      loadTodos();
    } catch(e){
      console.log(e);
    }
  }
}

const resolver = ({ values }) => {
  const errors = {};

  if (!values.task) {
    errors.task = [{ message: 'Task is required.' }];
  }

  return {
    values, // (Optional) Used to pass current form values to submit event.
    errors
  };
};


</script>

<template>
  <Form v-slot="$form" class="flex mb-2 gap-4 items-center todo" :initialValues="initialValues" @submit="onFormSubmit" :resolver>
    <Checkbox name="done" type="submit" binary @click="toggleDone"/>
    <InputText name="task" type="text" placeholder="Your Task" fluid/>
    <Message v-if="$form.task?.invalid" severity="error" size="small" variant="simple">{{$form.task.error?.message}}</Message>
    <DatePicker v-model="dueDate" dateFormat="dd/mm/yy" />
    <Button label="Update" style="font-size: 1rem; color: #708090" severity="secondary" type="submit"/> <!-- todo: only enable update button if valid and dirty -->
    <Button @click="console.log($form)"/>
    <Button @click="console.log('fff')"/>
  </Form>
</template>

<style scoped>
.todo {
  background: white;
  padding: 0.85rem;
}


</style>