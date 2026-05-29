package com.taskflow.model;

/**
 * Admin class - inherits from User (Inheritance)
 */
public class Admin extends User {
    private String adminCode;

    public Admin(String username, String password, String fullName, String adminCode) {
        super(username, password, fullName);
        this.adminCode = adminCode;
    }

    public String getAdminCode() { return adminCode; }
    public void setAdminCode(String adminCode) { this.adminCode = adminCode; }

    @Override
    public String toString() {
        return "Admin{username='" + getUsername() + "', fullName='" + getFullName() + "'}";
    }
}
