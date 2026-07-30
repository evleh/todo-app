package org.example.todoapp.controller;

import jakarta.validation.Valid;
import org.example.todoapp.dto.TodoCreateRequest;
import org.example.todoapp.dto.TodoResponse;
import org.example.todoapp.dto.TodoUpdateRequest;
import org.example.todoapp.security.UserPrincipal;
import org.example.todoapp.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/todos") 
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService){
        this.todoService = todoService;
    }

    @GetMapping // requires resource-level authorization i.e role-based authorization would be redundant
    public List<TodoResponse> readAll(@AuthenticationPrincipal UserPrincipal principal){
        return this.todoService.readAll(principal);
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasRole('ADMIN') || returnObject.ownerId().equals(authentication.principal.userId)")
    public TodoResponse read(@PathVariable String id){
       return this.todoService.read(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse create(@RequestBody @Valid TodoCreateRequest todo, @AuthenticationPrincipal UserPrincipal principal){
        return this.todoService.create(todo, principal);
    }

    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable String id, @RequestBody @Valid TodoUpdateRequest request, @AuthenticationPrincipal UserPrincipal principal){
        return this.todoService.update(id, request, principal);
    }

    @DeleteMapping("/{id}")
    public TodoResponse delete(@PathVariable String id, @AuthenticationPrincipal UserPrincipal principal){
        return this.todoService.deleteByID(id, principal);
    }

}
