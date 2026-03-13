package org.example.todoapp;

import org.example.todoapp.entity.Role;
import org.example.todoapp.repository.TodoRepository;
import org.example.todoapp.repository.UserRepository;
import org.example.todoapp.security.UserPrincipal;
import org.example.todoapp.service.TodoService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTests {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoService todoService;

    private static UserPrincipal adminUserPrincipal(){
        return new UserPrincipal("adminId", "adminUser", "password", List.of(new SimpleGrantedAuthority(Role.ADMIN.toGrantedAuthority())));
    }

    private static UserPrincipal regularUserPrincipal(){
        return new UserPrincipal("someUserId", "someUser", "password", List.of(new SimpleGrantedAuthority(Role.USER.toGrantedAuthority())));
    }


    @Nested
    class ReadAllTests {
        @Test
        void shouldReturnEverythingToAdmin(){}

        @Test
        void shouldReturnOwnToUser(){}

        @Test
        void shouldThrowWhenPrincipalIsNotInDB(){}


    }
}
