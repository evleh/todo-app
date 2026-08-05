package org.example.todoapp.dto;

import java.time.LocalDate;
import java.util.List;

public record TodoResponse(String id, String task, LocalDate due, boolean done, String ownerId, String parentId, List<TodoResponse> subtasks) {
}
