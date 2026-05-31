package com.taskflow.model;

import java.time.LocalDate;

public abstract class BaseTask {
    private int id;
    private String title;
    private String description;
    private String projectName;
    private String priority;
    private String status;
    private LocalDate deadline;

    public BaseTask() {}

    public BaseTask(int id, String title, String description, String projectName,
                    String priority, String status, LocalDate deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectName = projectName;
        this.priority = priority;
        this.status = status;
        this.deadline = deadline;
    }

    // Abstract method - must be implemented by subclasses (Polymorphism)
    public abstract boolean isOverdue();

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
}
