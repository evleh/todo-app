package org.example.todoapp.service;

import org.example.todoapp.entity.Todo;
import org.example.todoapp.repository.TodoRepository;
import org.example.todoapp.exception.TodoAlreadyExcistsException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo create (Todo todo){
        // check db for todo.name
        // check if due date is the same
        // throw error if true
        Optional<Todo> todoCheck = todoRepository.findByTask(todo.getTask());
        if(todo.isDone()){
            throw new TodoAlreadyExcistsException();
        }

        // else save to-do to db
        return todo;
    }
}
