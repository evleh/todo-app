package org.example.todoapp.repository;

import org.example.todoapp.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, String> {
    Optional<Todo> findByTask(String task);
}
