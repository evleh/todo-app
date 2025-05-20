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
 * - Einbinden: SpringDoc OpenAPI Starter WebMVC UI (in pom.xml)
 * - swagger: kann json doc aus meinem code für documentationmachen und mit swagger visualisieren (ist dann in resources/application.properties)
 *  und so kann ich routen aufrufen um das alles mal auszuprobieren
 *
 *
 *  Datenvalidierung:
 *  - über Annotationen in der Entity Klasse und @Valid Annotaton in Post methode
 *  - Validierung auch im Backend i.d. Business Logik, Frontend optional über js aber js kann man ausschalten
 *    wieso nicht auf DB verlagern: will DB austauschen können, je mehr ich in DB konfiguriere desto schwerer ist es sie später auszutauschen
 *  - ResponseStatus Annotation
 *
 *  - to-do Klassse ist in allen Schichten: cleane architektur hätte response-objekte in der Presentaion layer (Create-to-do: Validation object oder DTOs)
 *          Bsp.: wie ResponseObject i.d. Präsentation (so würde es Marvin machen): Erzeugt aus Post-Request (hat keine id) fertige Business-Logik Objekte und übergibt diese an Business-Schicht
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
