package org.example.todoapp.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String username;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Todo> todos = new ArrayList<>();

    public MyUser() {
        this.role = Role.USER;
    }

    public String getId() {
        return id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

