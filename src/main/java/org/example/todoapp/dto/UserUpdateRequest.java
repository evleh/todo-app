package org.example.todoapp.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(@NotBlank String username, @NotBlank String role) {
}
