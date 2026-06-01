package com.taskflow.service;

import com.taskflow.dao.UserDAO;
import com.taskflow.model.User;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private static User currentUser;

    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        User user = userDAO.authenticate(username.trim(), password.trim());
        if (user != null) {
            currentUser = user;
        }
        return user;
    }

    public String register(String username, String password, String fullName) {
        if (username == null || username.isBlank()) return "Username tidak boleh kosong!";
        if (username.length() < 4) return "Username minimal 4 karakter!";
        if (password == null || password.length() < 6) return "Password minimal 6 karakter!";
        if (fullName == null || fullName.isBlank()) return "Nama lengkap tidak boleh kosong!";
        if (userDAO.isUsernameExists(username.trim())) return "Username sudah dipakai, coba yang lain!";
        
        boolean ok = userDAO.register(username.trim(), password.trim(), fullName.trim());
        return ok ? null : "Gagal mendaftar, coba lagi.";
    }

    public static User getCurrentUser() { return currentUser; }
    public static void logout() { currentUser = null; }
}