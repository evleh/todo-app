package org.example.todoapp.service;

import org.example.todoapp.entity.Todo;
import org.example.todoapp.exception.TodoAlreadyExcistsException;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    public Todo create (Todo todo){
        // check db for todo.name
        // check if due date is the same
        // throw error if true

        if(todo.isDone()){
            throw new TodoAlreadyExcistsException();
        }

        // else save to-do to db
        return todo;
    }
}
