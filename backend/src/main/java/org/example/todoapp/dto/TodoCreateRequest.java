package org.example.todoapp.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TodoCreateRequest(@NotBlank String task, LocalDate due) {}
