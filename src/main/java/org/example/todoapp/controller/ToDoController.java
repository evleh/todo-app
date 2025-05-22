package org.example.todoapp.controller;

import jakarta.validation.Valid;
import org.example.todoapp.entity.Todo;
import org.example.todoapp.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**

 * Implementierung Controller
 *      - @RestController: RestController kombiniert Controller und ResponseBody (?)
 *      - @RequestMapping
 *      - @GetMapping, @PostMapping, @DeleteMapping, @PutMapping
 *      - @PathVariable
 *      - @RequestBody: Ist Teil von Http-Request der Daten überträgt
 *      - Input: Serialization/Deserialization: Datenumwandlung von Objekten und Formaten zur Datenübertragung über Netzwerke
 *      (z.b.: JSON, XML). Serialization (Java-Objekt zu JSON) / Deserialization (JSON zu Java-Object)
 * CRUD Controller
 * HTTP Methoden auf CRUD gemappt
 *
 * 20.5.:

 *
 *
 */
@RestController
@RequestMapping("/todos") // für Mapping von http-request für den ganzen Controller
public class ToDoController {

    private final TodoService todoService;

    public ToDoController(TodoService todoService){
        this.todoService = todoService;
    }


    @GetMapping
    public List<Todo> readAll(){
        return this.todoService.readAll();
    }

    @GetMapping("/{id}")
    public Todo read(@PathVariable String id){
       return this.todoService.read(id);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo create(@RequestBody @Valid Todo todo){
        return this.todoService.create(todo);
    }

    @PutMapping("/{id}")
    public Todo update(@PathVariable String id, @RequestBody Todo todo){
        return this.todoService.update(id, todo);
    }

    @DeleteMapping("/{id}")
    public Todo delete(@PathVariable String id){
        return this.todoService.deleteByID(id);
    }

}
