package org.example.todoapp.dto;

import java.time.LocalDateTime;

public record TodoUpdateRequest(String task, LocalDateTime due, boolean done) {
}
