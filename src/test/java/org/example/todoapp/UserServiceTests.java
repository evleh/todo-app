package org.example.todoapp;

import org.example.todoapp.dto.UserCreateRequest;
import org.example.todoapp.dto.UserResponse;
import org.example.todoapp.dto.UserUpdateRequest;
import org.example.todoapp.entity.MyUser;
import org.example.todoapp.entity.Role;
import org.example.todoapp.exception.EntityAlreadyExistsException;
import org.example.todoapp.exception.EntityNotFoundException;
import org.example.todoapp.repository.UserRepository;
import org.example.todoapp.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    class ReadAllTests{

        @Test
        void readAllOnEmptyRepository(){
            // arrange
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            // act
            List<UserResponse> users = userService.readAll();

            // assert
            assertTrue(users.isEmpty());
        }
    }

    @Nested
    class ReadTests{

        @Test
        void shouldReturnUserIfPresent(){
            // arrange
            MyUser user = new MyUser();
            // see docs/decisions/001-jpa-entity-id-testing-strategy.md
            ReflectionTestUtils.setField(user, "id", "someUUID");
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // act
            UserResponse returnObject = userService.read(user.getId());

            // assert
            assertEquals(user.getId(), returnObject.id());
        }


        @Test
        void shouldThrowWhenUserNotFound(){
            // arrange
            MyUser user = new MyUser();
            // see docs/decisions/001-jpa-entity-id-testing-strategy.md
            ReflectionTestUtils.setField(user, "id", "someUUID");

            when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

            // act & assert
            assertThrows(EntityNotFoundException.class, () -> userService.read(user.getId()));

        }
    }

    @Nested
    class CreateTests{

        @Test
        void shouldReturnNewUserOnSuccess(){
            // arrange
            UserCreateRequest request = new UserCreateRequest("username", "password");
            when(userRepository.findByUsername(request.username())).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password")).thenReturn("hashed_password");
            MyUser savedUser = new MyUser();
            // see docs/decisions/001-jpa-entity-id-testing-strategy.md
            ReflectionTestUtils.setField(savedUser, "id", "some-uuid");
            savedUser.setUsername(request.username());
            when(userRepository.save(any())).thenReturn(savedUser);
            // act
            UserResponse response = userService.create(request);

            // assert
            assertEquals(request.username(), response.username());
            assertEquals(savedUser.getId(), response.id());
            verify(userRepository).save(any());
            verify(passwordEncoder).encode("password");

        }

        @Test
        void shouldThrowIfUsernameAlreadyExists(){
            // arrange
            UserCreateRequest request = new UserCreateRequest("username", "password");
            when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(new MyUser()));

            // act & assert
            assertThrows(EntityAlreadyExistsException.class, () -> userService.create(request));
        }
    }

    @Nested
    class DeleteTests{

        @Test
        void shouldThrowWhenUserNotFoundInDelete(){
            // arrange
            String id = "some_UUID";
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            // act & assert
            assertThrows(EntityNotFoundException.class, () -> userService.delete(id));
        }

        @Test
        void shouldDeleteUserWhenFound(){
            // arrange
            MyUser user = new MyUser();
            // see docs/decisions/001-jpa-entity-id-testing-strategy.md
            ReflectionTestUtils.setField(user, "id", "some_UUID");
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // act
            UserResponse response = userService.delete(user.getId());

            // assert
            assertEquals(user.getId(), response.id());
            verify(userRepository).delete(any());
        }
    }

    @Nested
    class UpdateTests{
        @Test
        void shouldThrowWhenUserNotFoundInUpdate(){
            // arrange
            String id = "some_UUID";
            UserUpdateRequest request = new UserUpdateRequest("username", Role.USER);
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            // act & assert
            assertThrows(EntityNotFoundException.class, () -> userService.update(id, request));
        }

        @Test
        void shouldUpdateUserIfFound(){
            // arrange
            String id = "some_UUID";
            UserUpdateRequest request = new UserUpdateRequest("username", Role.USER);
            MyUser updatedUser = new MyUser();
            updatedUser.setUsername(request.username());
            // see docs/decisions/001-jpa-entity-id-testing-strategy.md
            ReflectionTestUtils.setField(updatedUser, "id", id);
            when(userRepository.findById(id)).thenReturn(Optional.of(updatedUser));
            when(userRepository.save(any())).thenReturn(updatedUser);

            // act
            UserResponse response = userService.update(id, request);

            // assert
            assertEquals(request.username(), response.username());
            assertEquals(request.role().name(), response.role());
            verify(userRepository).save(any());
        }
    }
}
