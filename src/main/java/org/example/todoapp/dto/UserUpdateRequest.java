package org.example.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.todoapp.entity.Role;

public record UserUpdateRequest(@NotBlank String username, @NotNull Role role) {
}
