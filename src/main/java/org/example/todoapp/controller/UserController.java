package org.example.todoapp.controller;

import jakarta.validation.Valid;
import org.example.todoapp.dto.UserCreateRequest;
import org.example.todoapp.dto.UserResponse;
import org.example.todoapp.dto.UserUpdateRequest;
import org.example.todoapp.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse create(@RequestBody @Valid UserCreateRequest registration) {
        return userService.create(registration);
    }

    @GetMapping
    public List<UserResponse> readAll(){
        return userService.readAll();
    }

    @GetMapping("/{id}")
    public UserResponse read(@PathVariable String id){
        return userService.read(id);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable String id, @RequestBody @Valid UserUpdateRequest request){
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public UserResponse delete(@PathVariable String id){
        return userService.delete(id);
    }
}
