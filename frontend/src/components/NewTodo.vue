<script setup lang="ts">
import {ref, onMounted} from "vue";
import {TodoService} from "../services/TodoService.ts";
import Button from "primevue/button";
import InputText from "primevue/inputtext";
import Message from "primevue/message";
import {Form} from "@primevue/forms";
import { useToast } from "primevue/usetoast";

const props = withDefaults(defineProps<{
  parentId: string | null;
  autoFocus?: boolean;
}>(), {parentId: null, autoFocus: false});

const taskInputRef = ref();

onMounted(() => {
  if (props.autoFocus) {
    taskInputRef.value?.$el?.focus();
  }
});

const toast = useToast();

const emit = defineEmits<{
  todoCreated: []
}>();

const initialValues = ref({
  task: '',
  due: ''
});

const onFormSubmit =  async ({valid, values}) => {
  if (!valid) return; 

  try {
    props.parentId ? 
      await TodoService.createSubtask({task: values.task, due: values.due}, props.parentId) : 
      await TodoService.create({task: values.task, due: values.due}); 
    
    emit('todoCreated');
  } catch (error){
    toast.add({ severity: 'error', summary: 'Error when creating new (Sub)task.', life: 3000 });
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
  <Form v-slot="$form" :initialValues :resolver @submit="onFormSubmit" class="flex gap-1 p-2 items-start">
    <div class="flex flex-col gap-1 grow">
      <InputText ref="taskInputRef" name="task" type="text" placeholder="New Task"/>
      <Message v-if="$form.task?.invalid" severity="error" size="small" variant="simple">{{ $form.task.error?.message }}</Message>
    </div>
    
    <InputText name="due" type="date" placeholder="due" />
    <Button type="submit" 
      icon="pi pi-plus"
    />
  </Form>
</template>

<style scoped>
</style>