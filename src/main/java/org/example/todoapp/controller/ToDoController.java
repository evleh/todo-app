package org.example.todoapp.controller;

import jakarta.validation.Valid;
import org.example.todoapp.entity.Todo;
import org.example.todoapp.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Starten mit Spring:
 * - Projekt erstellen mit Dependencies
 * - Projekt Setup anhand der 3-Layer architektur (Controller / entity / Repository (erst erstellt als benötigt wurde) )
 * - 3-Layer Architektur:
 *   - Controller: Verantwortlich dafür, dass Http-Request an korrekte Funktion in der Logik weiter geleitet wird
 *   - Domain: Mit entity (Objekte die in DB gespeichert werden) und service hier wird Logik implementiert
 *   - Repository: Data Layer
 * - Controller setup anhand von Rest (?) für erstes Setup wurde immer nur null zurück gegeben +
 *
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
