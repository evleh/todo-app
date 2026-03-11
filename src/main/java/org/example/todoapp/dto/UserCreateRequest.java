package org.example.todoapp.dto;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(@NotBlank String username, @NotBlank String password) {
}
