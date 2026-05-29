package com.taskflow.model;

import java.time.LocalDate;

/**
 * Abstract base class for tasks (Abstraction)
 */
public abstract class BaseTask {
    private int id;
    private String title;
    private String description;
    private LocalDate deadline;

    public BaseTask(int id, String title, String description, LocalDate deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }

    // Abstract method - Polymorphism
    public abstract String getStatusLabel();
    public abstract boolean isCompleted();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public boolean isOverdue() {
        return !isCompleted() && LocalDate.now().isAfter(deadline);
    }
}
