package com.taskflow.controller;

import com.taskflow.model.User;
import com.taskflow.service.AuthService;
import com.taskflow.util.SceneManager;
import com.taskflow.view.LoginView;
import javafx.scene.input.KeyCode;

public class LoginController {

    private final LoginView view;
    private final AuthService authService = new AuthService();

    public LoginController(LoginView view) {
        this.view = view;
    }

    public void initialize() {
        view.errorLabel.setVisible(false);
        view.regErrorLabel.setVisible(false);
        view.regSuccessLabel.setVisible(false);

        view.passwordField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) handleLogin(); });
        view.usernameField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) view.passwordField.requestFocus(); });
        view.regPasswordConfirmField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) handleRegister(); });

        view.tabLogin.setOnAction(e -> showLoginPane());
        view.tabRegister.setOnAction(e -> showRegisterPane());

        view.loginButton.setOnAction(e -> handleLogin());
        view.registerButton.setOnAction(e -> handleRegister());

        showLoginPane();
    }

    private void showLoginPane() {
        view.loginPane.setVisible(true); view.loginPane.setManaged(true);
        view.registerPane.setVisible(false); view.registerPane.setManaged(false);
        view.styleTabActive(view.tabLogin); view.styleTabInactive(view.tabRegister);
    }

    private void showRegisterPane() {
        view.loginPane.setVisible(false); view.loginPane.setManaged(false);
        view.registerPane.setVisible(true); view.registerPane.setManaged(true);
        view.styleTabActive(view.tabRegister); view.styleTabInactive(view.tabLogin);
    }

    private void handleLogin() {
        String username = view.usernameField.getText().trim();
        String password = view.passwordField.getText();

        view.errorLabel.setVisible(false);
        view.loginButton.setDisable(true);
        view.loginButton.setText("Memuat Workspace...");

        User user = authService.login(username, password);

        if (user != null) {
            SceneManager.switchTo("dashboard");
        } else {
            view.errorLabel.setText("Username atau password salah!");
            view.errorLabel.setVisible(true);
            view.passwordField.clear();
        }
        view.loginButton.setDisable(false);
        view.loginButton.setText("Masuk ke OS");
    }

    private void handleRegister() {
        view.regErrorLabel.setVisible(false); view.regSuccessLabel.setVisible(false);

        String username = view.regUsernameField.getText().trim();
        String password = view.regPasswordField.getText();
        String confirm = view.regPasswordConfirmField.getText();
        String fullName = view.regFullNameField.getText().trim();

        if (!password.equals(confirm)) {
            view.regErrorLabel.setText("Password dan konfirmasi tidak cocok!");
            view.regErrorLabel.setVisible(true);
            return;
        }

        view.registerButton.setDisable(true);
        view.registerButton.setText("Mendaftar...");

        String errorMsg = authService.register(username, password, fullName);

        if (errorMsg == null) {
            view.regSuccessLabel.setText("✅ Akun berhasil dibuat! Silakan login.");
            view.regSuccessLabel.setVisible(true);
            view.regUsernameField.clear(); view.regPasswordField.clear();
            view.regPasswordConfirmField.clear(); view.regFullNameField.clear();
        } else {
            view.regErrorLabel.setText(errorMsg);
            view.regErrorLabel.setVisible(true);
        }

        view.registerButton.setDisable(false);
        view.registerButton.setText("Daftar Sekarang");
    }
}