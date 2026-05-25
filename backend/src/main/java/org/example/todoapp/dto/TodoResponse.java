package org.example.todoapp.dto;

import java.time.LocalDate;

public record TodoResponse(String id, String task, LocalDate due, boolean done, String ownerId) {
}
