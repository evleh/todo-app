package org.example.todoapp.service;

import org.example.todoapp.dto.UserResponse;
import org.example.todoapp.exception.EntityAlreadyExistsException;
import org.example.todoapp.entity.MyUser;
import org.example.todoapp.dto.UserCreateRequest;
import org.example.todoapp.exception.EntityNotFoundException;
import org.example.todoapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    private static UserResponse toResponse(MyUser user){
        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }

    public List<UserResponse> readAll(){
        return userRepository.findAll().stream().map(myUser -> toResponse(myUser)).toList();
    }

    public UserResponse create(UserCreateRequest registration) {
        userRepository.findByUsername(registration.getUsername()).ifPresent(user -> {throw new EntityAlreadyExistsException();});

        MyUser user = new MyUser();
        user.setUsername(registration.getUsername());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));

        MyUser newUser = userRepository.save(user);
        return toResponse(newUser);
    }

    public UserResponse read(String id){
        MyUser user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return toResponse(user);
    }
}
