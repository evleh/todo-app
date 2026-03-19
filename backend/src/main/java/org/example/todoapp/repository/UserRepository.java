package org.example.todoapp.repository;

import org.example.todoapp.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository
        extends JpaRepository<MyUser, String> {

    Optional<MyUser> findByUsername(String username);
}