package org.example.todoapp.service;

import org.example.todoapp.entity.Todo;
import org.example.todoapp.exception.TodoIdNotFoundException;
import org.example.todoapp.repository.TodoRepository;
import org.example.todoapp.exception.TodoAlreadyExcistsException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo update(String id, Todo todo){
        //Todo: Methoden Körper implementieren
        return null;
    }


    public Todo create (Todo todo){
        // check db for todo.name
        // check if due date is the same
        // throw error if true
        Optional<Todo> todoCheck = todoRepository.findByTask(todo.getTask());
        if(todo.isDone()){
            throw new TodoAlreadyExcistsException();
        } else {
            // else save to-do to db
            return todoRepository.save(todo);
        }

    }

    public Todo read(String id){
        Optional<Todo> task = todoRepository.findById(id);
        if(task.isEmpty()){
            //throw new TodoIdNotFoundException("Todo with id: " + id + " not found");
            throw new TodoIdNotFoundException();
        }

        return task.get();
    }

    public List<Todo> readAll(){
        return todoRepository.findAll();
    }

    public Todo deleteByID(String id){
        Optional<Todo> task = todoRepository.findById(id);
        if(task.isEmpty()){
            //throw new TodoIdNotFoundException("Todo with id: " + id + " not found");
            throw new TodoIdNotFoundException();
        }
        todoRepository.deleteById(id);
        return task.get();

    }
}
