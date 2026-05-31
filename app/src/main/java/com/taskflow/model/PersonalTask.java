package com.taskflow.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tugas Mandiri - untuk mahasiswa yang mengerjakan tugas sendiri (bukan kelompok)
 */
public class PersonalTask extends BaseTask {
    private int userId;
    private String userName;
    private String category; // e.g. "Kuliah", "Pribadi", "Penelitian"
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private boolean isPersonal = true;

    public PersonalTask() {}

    public PersonalTask(int id, String title, String description, String category,
                        int userId, String userName, String priority,
                        String status, LocalDate deadline,
                        LocalDateTime completedAt, LocalDateTime createdAt) {
        super(id, title, description, category, priority, status, deadline);
        this.userId = userId;
        this.userName = userName;
        this.category = category;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
    }

    @Override
    public boolean isOverdue() {
        if ("Done".equals(getStatus())) return false;
        if (getDeadline() == null) return false;
        return LocalDate.now().isAfter(getDeadline());
    }

    public boolean isDueSoon() {
        if ("Done".equals(getStatus())) return false;
        if (getDeadline() == null) return false;
        LocalDate today = LocalDate.now();
        return !today.isAfter(getDeadline()) && getDeadline().isBefore(today.plusDays(3));
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean getIsPersonal() { return isPersonal; }

    // For TableView compatibility - assignedToName alias
    public String getAssignedToName() { return userName; }
    public String getProjectName() { return category; }

    @Override
    public String toString() {
        return String.format("PersonalTask[%d: %s | %s | %s]", getId(), getTitle(), getStatus(), getDeadline());
    }
}
