package org.example.todoapp.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String task;
    private LocalDate due;
    private boolean done;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private MyUser owner;

    public MyUser getOwner() {
        return owner;
    }

    public void setOwner(MyUser owner) {
        this.owner = owner;
    }

    public Todo(String task, LocalDate due) {
        this.task = task;
        this.due = due;
        this.done = false;
    }

    public Todo(String task, LocalDate due, MyUser owner) {
        this(task, due);
        this.owner = owner;
    }

    public Todo() {}

    public String getId() {
        return id;
    }



    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDate getDue() {
        return due;
    }

    public void setDue(LocalDate due) {
        this.due = due;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
