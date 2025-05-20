package org.example.todoapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller vs RestController
 * springboot kann auch nur webseite sein (i.e. HTML)
 * RestController wird für Datenaustausch verwendet
 */
@RestController
public class IndexController {

    // Methode wird auf einen Path gemappt i.e. wenn http request auf path "/" kommt mach das
    @RequestMapping("/")
    public String hello(){
        return "Hello World";
    }
}
