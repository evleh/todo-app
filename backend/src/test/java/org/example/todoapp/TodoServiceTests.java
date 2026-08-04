package org.example.todoapp;

import org.example.todoapp.dto.TodoCreateRequest;
import org.example.todoapp.dto.TodoResponse;
import org.example.todoapp.entity.MyUser;
import org.example.todoapp.entity.Role;
import org.example.todoapp.entity.Todo;
import org.example.todoapp.exception.EntityNotFoundException;
import org.example.todoapp.exception.InvalidSubtaskParentException;
import org.example.todoapp.exception.PermissionDeniedException;
import org.example.todoapp.exception.TodoIdNotFoundException;
import org.example.todoapp.repository.TodoRepository;
import org.example.todoapp.repository.UserRepository;
import org.example.todoapp.security.UserPrincipal;
import org.example.todoapp.service.TodoService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

        // see docs/adr/0001-jpa-entity-id-testing-strategy.md 
        ReflectionTestUtils.setField(owner, "id", ownerPrincipal.getUserId());

        Todo todo = new Todo("test TodoService", LocalDate.now(), owner);
        // see docs/adr/0001-jpa-entity-id-testing-strategy.md 
        ReflectionTestUtils.setField(todo, "id", "someTodoUUID1");

        return todo;
    }

    private static Todo todoOwnedByAdmin(){
        UserPrincipal ownerPrincipal = adminUserPrincipal();
        MyUser owner = new MyUser();
        owner.setUsername(ownerPrincipal.getUsername());
        // see docs/adr/0001-jpa-entity-id-testing-strategy.md 
        ReflectionTestUtils.setField(owner, "id", ownerPrincipal.getUserId());

        Todo todo = new Todo("test TodoService", LocalDate.now(), owner);
        // see docs/adr/0001-jpa-entity-id-testing-strategy.md 
        ReflectionTestUtils.setField(todo, "id", "someTodoUUID2");

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
            assertEquals(todoOfAdmin.getId(), response.get(1).id());
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
            when(todoRepository.findByOwner(any(MyUser.class))).thenReturn(todos);

            // act
            List<TodoResponse> response = todoService.readAll(userPrincipal);

            // assert
            verify(todoRepository).findByOwner(user);
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

        @Test
        void shouldReturnEmptyListWhenUserHasNoTodos() {
            // arrange
            UserPrincipal userPrincipal = regularUserPrincipal();
            MyUser user = new MyUser();
            user.setUsername(userPrincipal.getUsername());
            ReflectionTestUtils.setField(user, "id", userPrincipal.getUserId());

            when(userRepository.findById(userPrincipal.getUserId())).thenReturn(Optional.of(user));
            when(todoRepository.findByOwner(any(MyUser.class))).thenReturn(Collections.emptyList());

            // act
            List<TodoResponse> response = todoService.readAll(userPrincipal);

            // assert
            verify(todoRepository).findByOwner(user);
            assertTrue(response.isEmpty());
        }

        @Test
        void shouldReturnEmptyListWhenAdminAndNoTodosExist() {
            // arrange
            UserPrincipal admin = adminUserPrincipal();
            when(todoRepository.findAll()).thenReturn(Collections.emptyList());

            // act
            List<TodoResponse> response = todoService.readAll(admin);

            // assert
            assertTrue(response.isEmpty());
        }

    }

    @Nested
    class CreateSubtaskTest {
        @Test
        void shouldReturnTodoResponseOnHappyPath(){
            // arrange
            UserPrincipal creator = regularUserPrincipal();
            Todo parent = todoOwnedBySomeUser();
            TodoCreateRequest request = new TodoCreateRequest("buy milk", LocalDate.now());

            Todo savedSubtask = new Todo(request.task(), request.due(), parent.getOwner());
            // see docs/adr/0001-jpa-entity-id-testing-strategy.md 
            ReflectionTestUtils.setField(savedSubtask, "id", "subtaskUUID1");

            when(todoRepository.findById(parent.getId())).thenReturn(Optional.of(parent));
            when(todoRepository.save(any(Todo.class))).thenReturn(savedSubtask);

            // act
            TodoResponse response = todoService.createSubtask(request, creator, parent.getId());

            // assert
            assertEquals(savedSubtask.getId(), response.id());
            assertEquals(request.task(), response.task());
            assertEquals(request.due(), response.due());
            assertEquals(parent.getOwner().getId(), response.ownerId());

            ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
            verify(todoRepository).save(captor.capture());
            assertEquals(parent.getOwner().getId(), captor.getValue().getOwner().getId());
        }

        @Test
        void shouldReturnTodoResponseIfParentOwnerIsNotSubtaskCreatorButAdmin(){
            // arrange
            UserPrincipal admin = adminUserPrincipal();
            Todo parent = todoOwnedBySomeUser(); // owned by a regular user, not the admin
            TodoCreateRequest request = new TodoCreateRequest("buy milk", LocalDate.now());

            Todo savedSubtask = new Todo(request.task(), request.due(), parent.getOwner());
            // see docs/adr/0001-jpa-entity-id-testing-strategy.md 
            ReflectionTestUtils.setField(savedSubtask, "id", "subtaskUUID2");

            when(todoRepository.findById(parent.getId())).thenReturn(Optional.of(parent));
            when(todoRepository.save(any(Todo.class))).thenReturn(savedSubtask);

            // act
            TodoResponse response = todoService.createSubtask(request, admin, parent.getId());

            // assert — subtask belongs to the parent's owner, not to the admin who created it
            assertEquals(parent.getOwner().getId(), response.ownerId());

            ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
            verify(todoRepository).save(captor.capture());
            assertEquals(parent.getOwner().getId(), captor.getValue().getOwner().getId());
        }

        @Test
        void shouldThrowIfParentIsSubtask(){
            // arrange
            UserPrincipal creator = regularUserPrincipal();
            Todo grandparent = todoOwnedBySomeUser();
            Todo parent = todoOwnedBySomeUser();
            // parent is itself already a subtask — no setParent() exists yet, so set the field directly
            // see docs/adr/0001-jpa-entity-id-testing-strategy.md 
            ReflectionTestUtils.setField(parent, "parent", grandparent);
            TodoCreateRequest request = new TodoCreateRequest("buy milk", LocalDate.now());

            when(todoRepository.findById(parent.getId())).thenReturn(Optional.of(parent));

            // act & assert
            assertThrows(InvalidSubtaskParentException.class,
                    () -> todoService.createSubtask(request, creator, parent.getId()));
            verify(todoRepository, never()).save(any());
        }

        @Test
        void shouldThrowIfParentOwnerIsNotSubtaskCreatorForNonAdmins(){
            // arrange
            UserPrincipal creator = regularUserPrincipal();
            Todo parent = todoOwnedByAdmin(); // owned by a different user than the creator
            TodoCreateRequest request = new TodoCreateRequest("buy milk", LocalDate.now());

            when(todoRepository.findById(parent.getId())).thenReturn(Optional.of(parent));

            // act & assert
            assertThrows(PermissionDeniedException.class,
                    () -> todoService.createSubtask(request, creator, parent.getId()));
            verify(todoRepository, never()).save(any());
        }

        @Test
        void shouldThrowIfParentDoesNotExist(){
            // arrange
            UserPrincipal creator = regularUserPrincipal();
            TodoCreateRequest request = new TodoCreateRequest("buy milk", LocalDate.now());

            when(todoRepository.findById("missingParentId")).thenReturn(Optional.empty());

            // act & assert
            assertThrows(TodoIdNotFoundException.class,
                    () -> todoService.createSubtask(request, creator, "missingParentId"));
            verify(todoRepository, never()).save(any());
        }
    }
}
