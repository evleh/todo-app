package org.example.todoapp.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TodoUpdateRequest(@NotBlank String task, LocalDate due, boolean done) {
}
