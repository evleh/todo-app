package org.example.todoapp.service;

import org.example.todoapp.dto.TodoCreateRequest;
import org.example.todoapp.dto.TodoResponse;
import org.example.todoapp.dto.TodoUpdateRequest;
import org.example.todoapp.entity.Todo;
import org.example.todoapp.exception.TodoIdNotFoundException;
import org.example.todoapp.repository.TodoRepository;
import org.example.todoapp.exception.TodoAlreadyExcistsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


// Possible improvements: private helper method toResponse(Todo todo) to replace dublicated code
@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public TodoResponse update(String id, TodoUpdateRequest request){
        Todo todo = todoRepository.findById(id).orElseThrow(TodoIdNotFoundException::new);
        todo.setDone(request.done());
        todo.setDue(request.due());
        todo.setTask(request.task());
        Todo updated = todoRepository.save(todo);
        return new TodoResponse(updated.getId(), updated.getTask(), updated.getDue(), updated.isDone());
    }


    public TodoResponse create (TodoCreateRequest todo){
        Optional<Todo> todoCheck = todoRepository.findByTask(todo.task());
        if(todoCheck.isPresent()){
            throw new TodoAlreadyExcistsException();
        }
        Todo toSave = new Todo(todo.task(), todo.due());
        Todo saved = todoRepository.save(toSave);
        return new TodoResponse(saved.getId(), saved.getTask(), saved.getDue(), saved.isDone());
    }

    public TodoResponse read(String id){
        Todo todo = todoRepository.findById(id).orElseThrow(TodoIdNotFoundException::new);
        return new TodoResponse(todo.getId(), todo.getTask(), todo.getDue(), todo.isDone());
    }

    public List<TodoResponse> readAll(){
        return todoRepository.findAll().stream().map(todo ->
                new TodoResponse(todo.getId(), todo.getTask(), todo.getDue(), todo.isDone())).toList();
    }

    public TodoResponse deleteByID(String id){
        Todo todo = todoRepository.findById(id).orElseThrow(TodoIdNotFoundException::new);
        todoRepository.deleteById(id);
        return new TodoResponse(todo.getId(), todo.getTask(), todo.getDue(), todo.isDone());

    }
}
