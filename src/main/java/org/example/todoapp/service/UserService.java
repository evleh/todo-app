package org.example.todoapp.service;

import org.example.todoapp.exception.EntityAlreadyExistsException;
import org.example.todoapp.exception.EntityNotFoundException;
import org.example.todoapp.entity.MyUser;
import org.example.todoapp.dto.Registration;
import org.example.todoapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public MyUser register(Registration registration) {
        userRepository.findByUsername(registration.getUsername()).ifPresent(user -> {throw new EntityAlreadyExistsException();});

        MyUser user = new MyUser();
        user.setUsername(registration.getUsername());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));

        return userRepository.save(user);
    }

    public MyUser getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException::new);
    }
}
