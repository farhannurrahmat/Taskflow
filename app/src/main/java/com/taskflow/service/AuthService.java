package com.taskflow.service;

import com.taskflow.model.User;
import com.taskflow.model.Admin;

import java.util.ArrayList;
import java.util.List;

/**
 * AuthService - handles authentication logic
 */
public class AuthService {
    private static AuthService instance;
    private List<User> users = new ArrayList<>();
    private User currentUser;

    private AuthService() {
        // Default users
        users.add(new User("mahasiswa", "1234", "Budi Santoso"));
        users.add(new Admin("admin", "admin123", "Admin TaskFlow", "ADM001"));
    }

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    public boolean login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.checkPassword(password)) {
                currentUser = u;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
