<script setup lang="ts">
import InputText from 'primevue/inputtext';
import {Form} from "@primevue/forms";
import Checkbox from 'primevue/checkbox';
import Button from 'primevue/button';
import {ref, inject, computed} from 'vue';
import {TodoService} from "../services/TodoService.ts";
import DatePicker from 'primevue/datepicker';
import Message from "primevue/message";
import { useToast } from "primevue/usetoast";

const toast = useToast();
const props = defineProps(['todo']);
const initialValues = ref({ task: props.todo.task, done: props.todo.done });
const dueDate = ref<Date | null>(parseDue(props.todo.due));
const initialDueDate = parseDue(props.todo.due);
const isDueDateDirty = computed(() => {
  const cur = dueDate.value;
  if (cur === null && initialDueDate === null) return false;
  if (cur === null || initialDueDate === null) return true;
  return cur.getTime() !== initialDueDate.getTime();
});
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
    toast.add({ severity: 'error', summary: 'Error: Task could not be marked as done.', life: 3000 });
    console.log(e);
  }
}

const deleteTodo = async () => {
  try {
    await TodoService.delete(props.todo.id);
    loadTodos();
  } catch (e) {
    toast.add({ severity: 'error', summary: 'Error: Task could not be deleted.', life: 3000 });
    console.log(e);
  }
};

const onFormSubmit = async ({valid, values}: {valid: boolean; values: Record<string, unknown>}) => {
  if(valid){
    try{
      await TodoService.update({id: props.todo.id, task: values.task as string, due: formatDue(dueDate.value), done: values.done as boolean});
      loadTodos();
    } catch(e){
      toast.add({ severity: 'error', summary: 'Error: Updates to task could not be saved', life: 3000 });
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
  <Form v-slot="$form" class="flex-col m-2 justify-center" :initialValues="initialValues" @submit="onFormSubmit" :resolver>
    <div class="flex items-center todo gap-2">
      <Checkbox name="done" type="submit" binary @click="toggleDone"/>
      <InputText name="task" type="text" placeholder="Your Task" fluid/>
      <DatePicker v-model="dueDate" dateFormat="dd-mm-yy" placeholder="No due date" />
      <Button icon="pi pi-trash" aria-label="Delete" style="color: #708090" severity="secondary" @click="deleteTodo"/>
      <Button icon="pi pi-save" aria-label="Update" style="color: #708090" severity="secondary" type="submit"
              v-tooltip.top="{ value: 'Save updated todo.', escape: false}"
              :disabled="!$form.task?.dirty && !$form.done?.dirty && !isDueDateDirty"/>
    </div>
    <div class="pl-[1.75rem]">
      <Message v-if="$form.task?.invalid" severity="error" size="small" variant="simple">{{$form.task.error?.message}}</Message>
    </div>
  </Form>
</template>

<style scoped>
.todo {
  background: #1112;
  opacity: 1;
  padding: 0.5rem;
  border-radius: 10px;
}


</style>