package org.example.todoapp.controller;

import jakarta.validation.Valid;
import org.example.todoapp.entity.Todo;
import org.example.todoapp.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD Controller
 * HTTP Methoden auf CRUD gemappt
 *
 * 20.5.: Implementierung der CRUD-Methoden
 *
 *
 *
 *  Datenvalidierung:

 *
 *
 */
@RestController
@RequestMapping("/todos")
public class ToDoController {

    private final TodoService todoService;

    public ToDoController(TodoService todoService){
        this.todoService = todoService;
    }


    @GetMapping
    public List<Todo> readAll(){
        return null;
    }

    @GetMapping("/{id}")
    public Todo read(@PathVariable String id){
        return null;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo create(@RequestBody @Valid Todo todo){
        return this.todoService.create(todo);
    }

    @PutMapping("/{id}")
    public Todo update(@PathVariable String id, @RequestBody Todo todo){
        return null;
    }

    @DeleteMapping("/{id}")
    public Todo delete(@PathVariable String id){
        return null;
    }

}
