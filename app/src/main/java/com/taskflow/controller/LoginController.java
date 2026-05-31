package com.taskflow.controller;

import com.taskflow.model.User;
import com.taskflow.service.AuthService;
import com.taskflow.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

public class LoginController {

    // Login fields
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    // Register fields
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regPasswordConfirmField;
    @FXML private TextField regFullNameField;
    @FXML private Label regErrorLabel;
    @FXML private Label regSuccessLabel;
    @FXML private Button registerButton;

    // Tab controls
    @FXML private VBox loginPane;
    @FXML private VBox registerPane;
    @FXML private Button tabLogin;
    @FXML private Button tabRegister;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        regErrorLabel.setVisible(false);
        regSuccessLabel.setVisible(false);

        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) passwordField.requestFocus();
        });

        showLoginPane();
    }

    @FXML
    private void showLoginPane() {
        loginPane.setVisible(true);
        loginPane.setManaged(true);
        registerPane.setVisible(false);
        registerPane.setManaged(false);
        tabLogin.getStyleClass().remove("tab-inactive");
        tabLogin.getStyleClass().add("tab-active");
        tabRegister.getStyleClass().remove("tab-active");
        tabRegister.getStyleClass().add("tab-inactive");
    }

    @FXML
    private void showRegisterPane() {
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        registerPane.setVisible(true);
        registerPane.setManaged(true);
        tabRegister.getStyleClass().remove("tab-inactive");
        tabRegister.getStyleClass().add("tab-active");
        tabLogin.getStyleClass().remove("tab-active");
        tabLogin.getStyleClass().add("tab-inactive");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        errorLabel.setVisible(false);
        loginButton.setDisable(true);
        loginButton.setText("Masuk...");

        User user = authService.login(username, password);

        if (user != null) {
            SceneManager.switchTo("dashboard");
        } else {
            errorLabel.setText("Username atau password salah!");
            errorLabel.setVisible(true);
            passwordField.clear();
        }

        loginButton.setDisable(false);
        loginButton.setText("Masuk");
    }

    @FXML
    private void handleRegister() {
        regErrorLabel.setVisible(false);
        regSuccessLabel.setVisible(false);

        String username = regUsernameField.getText();
        String password = regPasswordField.getText();
        String confirm = regPasswordConfirmField.getText();
        String fullName = regFullNameField.getText();

        if (!password.equals(confirm)) {
            regErrorLabel.setText("Password dan konfirmasi tidak cocok!");
            regErrorLabel.setVisible(true);
            return;
        }

        registerButton.setDisable(true);
        registerButton.setText("Mendaftar...");

        String errorMsg = authService.register(username, password, fullName);

        if (errorMsg == null) {
            regSuccessLabel.setText("✅ Akun berhasil dibuat! Silakan login.");
            regSuccessLabel.setVisible(true);
            regUsernameField.clear();
            regPasswordField.clear();
            regPasswordConfirmField.clear();
            regFullNameField.clear();
        } else {
            regErrorLabel.setText(errorMsg);
            regErrorLabel.setVisible(true);
        }

        registerButton.setDisable(false);
        registerButton.setText("Daftar Sekarang");
    }
}
