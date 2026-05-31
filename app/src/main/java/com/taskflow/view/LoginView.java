package com.taskflow.view;

import com.taskflow.controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Programmatic JavaFX replacement for login.fxml
 * Professional dark-themed login with tab switching
 */
public class LoginView extends StackPane {

    // Login fields
    public final TextField usernameField = new TextField();
    public final PasswordField passwordField = new PasswordField();
    public final Label errorLabel = new Label();
    public final Button loginButton = new Button("Masuk");

    // Register fields
    public final TextField regFullNameField = new TextField();
    public final TextField regUsernameField = new TextField();
    public final PasswordField regPasswordField = new PasswordField();
    public final PasswordField regPasswordConfirmField = new PasswordField();
    public final Label regErrorLabel = new Label();
    public final Label regSuccessLabel = new Label();
    public final Button registerButton = new Button("Daftar Sekarang");

    // Tab controls
    public final Button tabLogin = new Button("Masuk");
    public final Button tabRegister = new Button("Daftar Akun");
    public final VBox loginPane = new VBox();
    public final VBox registerPane = new VBox();

    public LoginView() {
        buildUI();
    }

    private void buildUI() {
        // Root background — deep navy gradient
        setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #0f172a 0%, #1e3a5f 50%, #0f172a 100%);"
        );
        setPrefSize(900, 640);

        // Decorative background circles (subtle)
        Pane decorLayer = new Pane();
        decorLayer.setMouseTransparent(true);
        decorLayer.setStyle("-fx-background-color: transparent;");
        addDecorations(decorLayer);

        // Card container
        VBox card = buildCard();

        getChildren().addAll(decorLayer, card);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    private void addDecorations(Pane pane) {
        Circle c1 = new Circle(220);
        c1.setFill(Color.web("#1e3a5f", 0.4));
        c1.setLayoutX(-80);
        c1.setLayoutY(80);

        Circle c2 = new Circle(160);
        c2.setFill(Color.web("#2563eb", 0.12));
        c2.setLayoutX(860);
        c2.setLayoutY(500);

        Rectangle rect = new Rectangle(300, 300);
        rect.setArcWidth(60);
        rect.setArcHeight(60);
        rect.setFill(Color.web("#0ea5e9", 0.06));
        rect.setRotate(30);
        rect.setLayoutX(700);
        rect.setLayoutY(-100);

        pane.getChildren().addAll(c1, c2, rect);
    }

    private VBox buildCard() {
        VBox card = new VBox();
        card.setMaxWidth(440);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.40), 48, 0, 0, 12);"
        );

        // Brand header
        VBox brand = buildBrand();

        // Tab bar
        HBox tabBar = buildTabBar();

        // Forms
        buildLoginPane();
        buildRegisterPane();

        card.getChildren().addAll(brand, tabBar, loginPane, registerPane);
        return card;
    }

    private VBox buildBrand() {
        VBox brand = new VBox(6);
        brand.setAlignment(Pos.CENTER);
        brand.setPadding(new Insets(36, 28, 26, 28));
        brand.setStyle(
            "-fx-background-color: #0f172a;" +
            "-fx-background-radius: 20 20 0 0;"
        );

        Label icon = new Label("✅");
        icon.setStyle("-fx-font-size: 40px;");

        Label title = new Label("TaskFlow");
        title.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Segoe UI', 'Helvetica Neue', sans-serif;"
        );

        Label subtitle = new Label("Platform Manajemen Tugas Mahasiswa");
        subtitle.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #64748b;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        );

        brand.getChildren().addAll(icon, title, subtitle);
        return brand;
    }

    private HBox buildTabBar() {
        HBox bar = new HBox(0);
        bar.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-border-color: transparent transparent #e2e8f0 transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        styleTabActive(tabLogin);
        styleTabInactive(tabRegister);

        HBox.setHgrow(tabLogin, Priority.ALWAYS);
        HBox.setHgrow(tabRegister, Priority.ALWAYS);
        tabLogin.setMaxWidth(Double.MAX_VALUE);
        tabRegister.setMaxWidth(Double.MAX_VALUE);

        bar.getChildren().addAll(tabLogin, tabRegister);
        return bar;
    }

    public void styleTabActive(Button btn) {
        btn.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 0;" +
            "-fx-background-color: white;" +
            "-fx-text-fill: #2563eb;" +
            "-fx-border-color: transparent transparent #2563eb transparent;" +
            "-fx-border-width: 0 0 3 0;" +
            "-fx-background-radius: 0;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        );
    }

    public void styleTabInactive(Button btn) {
        btn.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 0;" +
            "-fx-background-color: #f8fafc;" +
            "-fx-text-fill: #64748b;" +
            "-fx-border-color: transparent;" +
            "-fx-background-radius: 0;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        );
    }

    private void buildLoginPane() {
        loginPane.setSpacing(14);
        loginPane.setPadding(new Insets(24, 32, 28, 32));

        loginPane.getChildren().addAll(
            buildFieldGroup("Username", usernameField, "Masukkan username Anda"),
            buildFieldGroup("Password", passwordField, "Masukkan password Anda"),
            buildErrorLabel(errorLabel),
            buildPrimaryButton(loginButton, "Masuk")
        );
    }

    private void buildRegisterPane() {
        registerPane.setSpacing(12);
        registerPane.setPadding(new Insets(20, 32, 28, 32));
        registerPane.setVisible(false);
        registerPane.setManaged(false);

        registerPane.getChildren().addAll(
            buildFieldGroup("Nama Lengkap", regFullNameField, "Masukkan nama lengkap"),
            buildFieldGroup("Username", regUsernameField, "Minimal 4 karakter"),
            buildFieldGroup("Password", regPasswordField, "Minimal 6 karakter"),
            buildFieldGroup("Konfirmasi Password", regPasswordConfirmField, "Ulangi password"),
            buildErrorLabel(regErrorLabel),
            buildSuccessLabel(regSuccessLabel),
            buildSuccessButton(registerButton, "Daftar Sekarang")
        );
    }

    private VBox buildFieldGroup(String labelText, Control field, String prompt) {
        VBox group = new VBox(5);

        Label label = new Label(labelText);
        label.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #374151;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        );

        String fieldStyle =
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 14;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #d1d5db;" +
            "-fx-border-radius: 8;" +
            "-fx-background-color: #f9fafb;" +
            "-fx-font-family: 'Segoe UI', sans-serif;";

        if (field instanceof TextField tf) {
            tf.setPromptText(prompt);
            tf.setStyle(fieldStyle);
            tf.setPrefHeight(40);
            tf.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (isFocused) {
                    tf.setStyle(fieldStyle +
                        "-fx-border-color: #2563eb;" +
                        "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.18), 6, 0, 0, 0);");
                } else {
                    tf.setStyle(fieldStyle);
                }
            });
        }

        group.getChildren().addAll(label, field);
        return group;
    }

    private Label buildErrorLabel(Label lbl) {
        lbl.setStyle(
            "-fx-text-fill: #dc2626;" +
            "-fx-font-size: 12px;" +
            "-fx-background-color: #fef2f2;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 10;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        );
        lbl.setWrapText(true);
        lbl.setVisible(false);
        lbl.setManaged(false);
        // sync managed with visible
        lbl.visibleProperty().addListener((obs, o, v) -> lbl.setManaged(v));
        return lbl;
    }

    private Label buildSuccessLabel(Label lbl) {
        lbl.setStyle(
            "-fx-text-fill: #16a34a;" +
            "-fx-font-size: 12px;" +
            "-fx-background-color: #f0fdf4;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 10;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        );
        lbl.setWrapText(true);
        lbl.setVisible(false);
        lbl.setManaged(false);
        lbl.visibleProperty().addListener((obs, o, v) -> lbl.setManaged(v));
        return lbl;
    }

    private Button buildPrimaryButton(Button btn, String text) {
        btn.setText(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(44);
        btn.setStyle(
            "-fx-background-color: #2563eb;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #1d4ed8;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: #2563eb;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        ));
        return btn;
    }

    private Button buildSuccessButton(Button btn, String text) {
        btn.setText(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(44);
        btn.setStyle(
            "-fx-background-color: #16a34a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #15803d;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: #16a34a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        ));
        return btn;
    }
}
