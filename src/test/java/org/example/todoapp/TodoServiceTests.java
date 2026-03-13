package org.example.todoapp;

import org.apache.catalina.User;
import org.example.todoapp.dto.TodoResponse;
import org.example.todoapp.entity.MyUser;
import org.example.todoapp.entity.Role;
import org.example.todoapp.entity.Todo;
import org.example.todoapp.exception.EntityNotFoundException;
import org.example.todoapp.repository.TodoRepository;
import org.example.todoapp.repository.UserRepository;
import org.example.todoapp.security.UserPrincipal;
import org.example.todoapp.service.TodoService;
import org.hibernate.grammars.hql.HqlParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTests {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoService todoService;

    private static UserPrincipal adminUserPrincipal(){
        return new UserPrincipal("adminUUID", "adminUser", "password", List.of(new SimpleGrantedAuthority(Role.ADMIN.toGrantedAuthority())));
    }

    private static UserPrincipal regularUserPrincipal(){
        return new UserPrincipal("someUserUUID", "someUser", "password", List.of(new SimpleGrantedAuthority(Role.USER.toGrantedAuthority())));
    }

    private static Todo todoOwnedBySomeUser(){
        UserPrincipal ownerPrincipal = regularUserPrincipal();
        MyUser owner = new MyUser();
        owner.setUsername(ownerPrincipal.getUsername());
        ReflectionTestUtils.setField(owner, "id", ownerPrincipal.getUserId());

        Todo todo = new Todo("test TodService", LocalDateTime.now(), owner);
        ReflectionTestUtils.setField(todo, "id", "someTodoUUID");

        return todo;
    }

    private static Todo todoOwnedByAdmin(){
        UserPrincipal ownerPrincipal = adminUserPrincipal();
        MyUser owner = new MyUser();
        owner.setUsername(ownerPrincipal.getUsername());
        ReflectionTestUtils.setField(owner, "id", ownerPrincipal.getUserId());

        Todo todo = new Todo("test TodService", LocalDateTime.now(), owner);
        ReflectionTestUtils.setField(todo, "id", "someTodoUUID");

        return todo;
    }



    @Nested
    class ReadAllTests {
        @Test
        void shouldReturnEverythingToAdmin(){
            // arrange
            UserPrincipal admin = adminUserPrincipal();
            List<Todo> todos = new ArrayList<>();
            Todo todoOfUser = todoOwnedBySomeUser();
            Todo todoOfAdmin = todoOwnedByAdmin();
            todos.add(todoOfUser);
            todos.add(todoOfAdmin);
            when(todoRepository.findAll()).thenReturn(todos);

            // act
            List<TodoResponse> response = todoService.readAll(admin);

            // assert
            assertEquals(todoOfUser.getTask(), response.getFirst().task());
            assertEquals(todoOfUser.getId(), response.getFirst().id());
            assertEquals(todoOfUser.getOwner().getId(), response.getFirst().ownerId());
            assertEquals(todoOfUser.getDue(), response.getFirst().due());
            assertEquals(2, response.size());
        }

        @Test
        void shouldReturnOwnToUser(){
            // arrange
            UserPrincipal userPrincipal = regularUserPrincipal();
            MyUser user = new MyUser();
            user.setUsername(userPrincipal.getUsername());
            ReflectionTestUtils.setField(user, "id", userPrincipal.getUserId());

            List<Todo> todos = new ArrayList<>();
            Todo todoOfUser = todoOwnedBySomeUser();
            todos.add(todoOfUser);

            when(userRepository.findById(userPrincipal.getUserId())).thenReturn(Optional.of(user));
            when(todoRepository.findByOwner(user)).thenReturn(todos);

            // act
            List<TodoResponse> response = todoService.readAll(userPrincipal);
            System.out.println(response);

            // assert
            assertEquals(todoOfUser.getTask(), response.getFirst().task());
            assertEquals(todoOfUser.getId(), response.getFirst().id());
            assertEquals(todoOfUser.getOwner().getId(), response.getFirst().ownerId());
            assertEquals(todoOfUser.getDue(), response.getFirst().due());
            assertEquals(1, response.size());
        }

        @Test
        void shouldThrowWhenPrincipalIsNotInDB(){
            // arrange
            UserPrincipal userPrincipal = regularUserPrincipal();
            when(userRepository.findById(userPrincipal.getUserId())).thenReturn(Optional.empty());

            // act & assert
            assertThrows(EntityNotFoundException.class, () -> todoService.readAll(userPrincipal));
        }


    }
}
