package org.example.todoapp.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record TodoCreateRequest(@NotBlank String task, LocalDateTime due) {}
