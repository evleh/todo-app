package org.example.todoapp.controller;

import jakarta.validation.Valid;
import org.example.todoapp.dto.Registration;
import org.example.todoapp.entity.MyUser;
import org.example.todoapp.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public MyUser create(@RequestBody @Valid Registration registration) {
        return userService.register(registration);
    }
}
