package com.taskflow.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task extends BaseTask {
    private int assignedTo;
    private String assignedToName;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    public Task() {}

    public Task(int id, String title, String description, String projectName,
                int assignedTo, String assignedToName, String priority,
                String status, LocalDate deadline, LocalDateTime completedAt, LocalDateTime createdAt) {
        super(id, title, description, projectName, priority, status, deadline);
        this.assignedTo = assignedTo;
        this.assignedToName = assignedToName;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
    }

    // Polymorphism - Override abstract method from BaseTask
    @Override
    public boolean isOverdue() {
        if ("Done".equals(getStatus())) return false;
        if (getDeadline() == null) return false;
        return LocalDate.now().isAfter(getDeadline());
    }

    public int getAssignedTo() { return assignedTo; }
    public void setAssignedTo(int assignedTo) { this.assignedTo = assignedTo; }

    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatusDisplay() {
        return getStatus();
    }

    @Override
    public String toString() {
        return String.format("Task[%d: %s | %s | %s]", getId(), getTitle(), getStatus(), getDeadline());
    }
}
