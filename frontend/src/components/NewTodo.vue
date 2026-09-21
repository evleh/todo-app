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

const onFormSubmit =  async ({valid, values}: {valid: boolean; values: Record<string, unknown>}) => {
  if (!valid) return;

  const request = {task: values.task as string, due: values.due as string};

  try {
    props.parentId ?
      await TodoService.createSubtask(request, props.parentId) :
      await TodoService.create(request);
    
    emit('todoCreated');
  } catch (error){
    toast.add({ severity: 'error', summary: 'Error when creating new (Sub)task.', life: 3000 });
  }
}

const resolver = ({ values }: {values: Record<string, unknown>}) => {
  const errors: Record<string, { message: string }[]> = {};

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