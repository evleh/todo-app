package org.example.todoapp.repository;

import org.example.todoapp.entity.MyUser;
import org.example.todoapp.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, String> {
    Optional<Todo> findByTask(String task);

    List<Todo> findByOwner(MyUser owner);
}
