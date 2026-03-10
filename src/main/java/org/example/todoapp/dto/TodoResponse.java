package org.example.todoapp.dto;

import java.time.LocalDateTime;

public record TodoResponse(String id, String task, LocalDateTime due, boolean done) {
}
