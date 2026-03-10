package org.example.todoapp.controller;

import jakarta.validation.Valid;
import org.example.todoapp.dto.TodoCreateRequest;
import org.example.todoapp.dto.TodoResponse;
import org.example.todoapp.dto.TodoUpdateRequest;
import org.example.todoapp.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService){
        this.todoService = todoService;
    }


    @GetMapping
    public List<TodoResponse> readAll(){
        return this.todoService.readAll();
    }

    @GetMapping("/{id}")
    public TodoResponse read(@PathVariable String id){
       return this.todoService.read(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse create(@RequestBody @Valid TodoCreateRequest todo){
        return this.todoService.create(todo);
    }

    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable String id, @RequestBody @Valid TodoUpdateRequest request){
        return this.todoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public TodoResponse delete(@PathVariable String id){
        return this.todoService.deleteByID(id);
    }

}
