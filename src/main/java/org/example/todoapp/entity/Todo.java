package org.example.todoapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Todo {

    @NotBlank
    private String task;
    private LocalDateTime due;
    private boolean done;

    @Id
    private String id;

    public Todo(String task, LocalDateTime due, boolean done) {
        this.id = UUID.randomUUID().toString();
        this.task = task;
        this.due = due;
        this.done = done;
    }

    public Todo(String task, LocalDateTime due) {
        this.id = UUID.randomUUID().toString();
        this.task = task;
        this.due = due;
        this.done = false;
    }

    public Todo() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }



    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDateTime getDue() {
        return due;
    }

    public void setDue(LocalDateTime due) {
        this.due = due;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
