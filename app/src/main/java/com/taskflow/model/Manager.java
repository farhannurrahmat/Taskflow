package com.taskflow.model;

public class Manager extends User {

    public Manager() {
        super();
        setRole("manager");
    }

    public Manager(int id, String username, String password, String fullName) {
        super(id, username, password, fullName, "manager");
    }

    public boolean canManageAllTasks() {
        return true;
    }

    public boolean canViewStatistics() {
        return true;
    }

    @Override
    public String toString() {
        return "[Manager] " + getFullName();
    }
}
