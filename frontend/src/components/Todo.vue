<script setup lang="ts">
import InputText from 'primevue/inputtext';
import {Form} from "@primevue/forms";
import Checkbox from 'primevue/checkbox';
import Button from 'primevue/button';
import {ref, inject} from 'vue';
import {TodoService} from "../services/TodoService.ts";
import DatePicker from 'primevue/datepicker';
import Message from "primevue/message";
import { useToast } from "primevue/usetoast";
import NewTodo from './NewTodo.vue';

const emit = defineEmits<{
  completed: []
  expandSubtasks: []
}>();

const toast = useToast();
const props = defineProps(['todo', 'isExpanded']);
const initialValues = ref({ task: props.todo.task, done: props.todo.done });
const dueDate = ref<Date | null>(parseDue(props.todo.due));
const loadTodos = inject<() => Promise<void>>('loadTodos', async () => {});
const showReward = inject<() => void>('showReward', () => {});

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
    const willBeDone = !props.todo.done; 
    
    await TodoService.update({id: props.todo.id, task: props.todo.task, due: props.todo.due, done: willBeDone});
    await loadTodos();
    
    if (willBeDone) {
      showReward(); 
    }
    
  } catch(e){
    toast.add({ severity: 'error', summary: 'Error: Task could not be marked as done.', life: 3000 });
    console.log(e);
  }
}

const deleteTodo = async () => {
  try {
    await TodoService.delete(props.todo.id);
    await loadTodos();
  } catch (e) {
    toast.add({ severity: 'error', summary: 'Error: Task could not be deleted.', life: 3000 });
    console.log(e);
  }
};

const saveTodo = async (task: string, due: string | null) => {
  try {
    await TodoService.update({id: props.todo.id, task, due, done: props.todo.done});
    await loadTodos();
  } catch (e) {
    toast.add({ severity: 'error', summary: 'Error: Updates to task could not be saved', life: 3000 });
    console.log(e);
  }
}

// auto-save: text/due commit on blur, not via an explicit save button
const onFormBlur = ($form: any) => {
  if ($form.task?.invalid) return;
  saveTodo($form.task?.value, formatDue(dueDate.value));
}

const onFormSubmit = ({valid, values}) => {
  if (!valid) return;
  saveTodo(values.task as string, formatDue(dueDate.value));
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

// subtasks
const isAddingSubtask = ref(false); 

const createSubtask = () => {
  isAddingSubtask.value = true;
  emit('expandSubtasks');
}

const onSubtaskCreated = async () => {
  isAddingSubtask.value = false;
  await loadTodos();
}

const onSubtaskFormFocusOut = (e: FocusEvent) => {
  const container = e.currentTarget as HTMLElement;
  if (!container.contains(e.relatedTarget as Node)) {
    isAddingSubtask.value = false;
  }
}


</script>

<template>
  <div class="todo">
    <div>


      <Form v-slot="$form" class="flex-col justify-center" :initialValues="initialValues" @submit="onFormSubmit" :resolver>
        <div class="group flex items-center gap-2">
          <Checkbox name="done" binary @click="toggleDone"/>
          <InputText name="task" type="text" placeholder="Your Task" fluid
            :class="{ 'task-done': props.todo.done }"
            @blur="onFormBlur($form)"
          />
          <DatePicker v-model="dueDate" dateFormat="dd-mm-yy" placeholder="No due date" class="w-36"
            @date-select="onFormBlur($form)"
            @blur="onFormBlur($form)"
          />
          <div class="row-actions w-16 flex items-center justify-end gap-1 opacity-40 group-hover:opacity-100 group-focus-within:opacity-100 transition-opacity">
            <Button icon="pi pi-plus"
              v-if="props.todo?.parentId == null"
              text
              severity="secondary"
              @click="createSubtask"
              v-tooltip.top="{ value: 'Add subtask.', escape: false}"
              aria-label="Add Subtask"
            />
            <Button icon="pi pi-trash" aria-label="Delete" text severity="danger" @click="deleteTodo"/>
          </div>
        </div>
        <div class="pl-[1.75rem]">
          <Message v-if="$form.task?.invalid" severity="error" size="small" variant="simple">{{$form.task.error?.message}}</Message>
        </div>

      </Form>
    </div>

    <NewTodo v-if="isAddingSubtask && isExpanded"
      :parent-id="props.todo.id"
      :auto-focus="true"
      @todo-created="onSubtaskCreated"
      @focusout="onSubtaskFormFocusOut"
      class="create-subtask"
    />
  </div>
  
</template>

<style scoped>
.todo {
  /*
  background: #1112;
  opacity: 1;
  */
  padding: 0.25rem;
  border-radius: 10px;
}

.create-subtask {
  margin-left: 1.25rem;
  margin-right: -0.5rem;
  margin-top: 0.25rem;
  padding: 0.25rem 0.5rem;
  border: 1px dashed var(--accent-border);
  background: var(--accent-bg);
  border-radius: 10px;
}

.task-done {
  color: var(--text) !important;
  text-decoration: line-through ;
  text-decoration-color: var(--text);
}


</style>