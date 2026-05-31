package com.taskflow.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


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
        // Root background — subtle modern grayish-blue
        setStyle("-fx-background-color: #f1f5f9;");

        // Decoration Pane (Placed behind the card)
        Pane decorPane = new Pane();
        addDecorations(decorPane);

        // Card container
        VBox card = buildCard();

        // Add both decorations and card to StackPane
        getChildren().addAll(decorPane, card);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    private void addDecorations(Pane pane) {
        Circle c1 = new Circle(220);
        c1.setFill(Color.web("#2563eb", 0.08));
        c1.setLayoutX(50);
        c1.setLayoutY(80);

        Circle c2 = new Circle(160);
        c2.setFill(Color.web("#3b82f6", 0.12));
        c2.setLayoutX(950);
        c2.setLayoutY(600);

        Rectangle rect = new Rectangle(300, 300);
        rect.setArcWidth(60);
        rect.setArcHeight(60);
        rect.setFill(Color.web("#0ea5e9", 0.06));
        rect.setRotate(35);
        rect.setLayoutX(750);
        rect.setLayoutY(-50);

        pane.getChildren().addAll(c1, c2, rect);
    }

    private VBox buildCard() {
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(30));

        HBox content = new HBox();
        content.setPrefSize(1000, 550);
        content.setMaxSize(1000, 550);

        content.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 30, 0, 0, 10);");

        VBox leftPanel = buildLeftPanel();
        leftPanel.setPrefWidth(380);

        VBox rightPanel = new VBox();
        rightPanel.setPrefWidth(620);

        HBox tabBar = buildTabBar();

        buildLoginPane();
        buildRegisterPane();

        rightPanel.getChildren().addAll(
                tabBar,
                loginPane,
                registerPane);

        content.getChildren().addAll(
                leftPanel,
                rightPanel);

        wrapper.getChildren().add(content);

        return wrapper;
    }

    private VBox buildLeftPanel() {
        VBox left = new VBox(22);
        left.setPrefWidth(380);
        left.setPadding(new Insets(50, 40, 50, 40));
        left.setAlignment(Pos.CENTER_LEFT);
        left.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1e40af, #2563eb);" +
                "-fx-background-radius: 20 0 0 20;");

        StackPane logoPane = new StackPane();
        logoPane.setAlignment(Pos.CENTER_LEFT);
        Circle logoBg = new Circle(28, Color.web("#ffffff", 0.15));
        Label logoText = new Label("TF");
        logoText.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");
        logoPane.getChildren().addAll(logoBg, logoText);

        Label title = new Label("TaskFlow");
        title.setStyle(
                "-fx-font-size: 38px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");

        Label subtitle = new Label("Platform Manajemen Tugas Mahasiswa");
        subtitle.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #bfdbfe;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");

        Separator separator = new Separator();
        separator.setStyle("-fx-opacity: 0.3;");
        separator.setMaxWidth(80);

        Label desc = new Label("Kelola tugas kuliah dengan lebih mudah, organisir deadline, dan tingkatkan produktivitas.");
        desc.setWrapText(true);
        desc.setMaxWidth(280);
        desc.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: normal;" +
                "-fx-text-fill: #f8fafc;" +
                "-fx-line-spacing: 0.4em;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");

        VBox features = new VBox(14);
        features.setPadding(new Insets(10, 0, 0, 0));

        features.getChildren().addAll(
                createFeatureLabel("✓  Organisir tugas & proyek"),
                createFeatureLabel("✓  Pantau deadline akurat"),
                createFeatureLabel("✓  Statistik produktivitas")
        );

        left.getChildren().addAll(
                logoPane,
                title,
                subtitle,
                separator,
                desc,
                features);

        return left;
    }

    private Label createFeatureLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle(
                "-fx-text-fill: #e2e8f0;" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");
        return lbl;
    }

    private HBox buildTabBar() {
        HBox bar = new HBox(0);
        bar.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: transparent transparent #e2e8f0 transparent;" +
                "-fx-border-width: 0 0 1 0;" +
                "-fx-background-radius: 0 20 0 0;"); // Match right corner radius

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
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 16 0;" +
                "-fx-background-color: white;" +
                "-fx-text-fill: #2563eb;" +
                "-fx-border-color: transparent transparent #2563eb transparent;" +
                "-fx-border-width: 0 0 3 0;" +
                "-fx-background-radius: 0 20 0 0;" +
                "-fx-cursor: hand;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");
    }

    public void styleTabInactive(Button btn) {
        btn.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 16 0;" +
                "-fx-background-color: #f8fafc;" +
                "-fx-text-fill: #64748b;" +
                "-fx-border-color: transparent;" +
                "-fx-background-radius: 0 20 0 0;" +
                "-fx-cursor: hand;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");
    }

    private void buildLoginPane() {
        loginPane.setSpacing(20);
        loginPane.setPadding(new Insets(50, 70, 50, 70));

        VBox headerBox = new VBox(5);
        Label welcomeTitle = new Label("Selamat Datang");
        welcomeTitle.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");

        Label welcomeDesc = new Label("Masuk untuk melanjutkan ke *dashboard* Anda");
        welcomeDesc.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #64748b;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");
        
        headerBox.getChildren().addAll(welcomeTitle, welcomeDesc);

        Hyperlink forgotPassword = new Hyperlink("Lupa Password?");
        forgotPassword.setStyle("-fx-text-fill: #2563eb; -fx-font-family: 'Segoe UI', sans-serif;");
        HBox forgotBox = new HBox(forgotPassword);
        forgotBox.setAlignment(Pos.CENTER_RIGHT);
        forgotBox.setPadding(new Insets(-10, 0, 0, 0));

        loginPane.getChildren().addAll(
                headerBox,
                buildFieldGroup("Username", usernameField, "Masukkan username"),
                buildFieldGroup("Password", passwordField, "Masukkan password"),
                forgotBox,
                buildErrorLabel(errorLabel),
                buildPrimaryButton(loginButton, "Masuk ke Dashboard")
        );
    }

    private void buildRegisterPane() {
        registerPane.setSpacing(14);
        registerPane.setPadding(new Insets(30, 70, 30, 70));
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
        VBox group = new VBox(8);

        Label label = new Label(labelText);
        label.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #475569;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");

        String fieldStyle = 
                "-fx-font-size: 14px;" +
                "-fx-padding: 12 16;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-radius: 8;" +
                "-fx-background-color: #f8fafc;" +
                "-fx-font-family: 'Segoe UI', sans-serif;";

        if (field instanceof TextField tf) {
            tf.setPromptText(prompt);
            tf.setStyle(fieldStyle);
            tf.setPrefHeight(48);
            tf.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (isFocused) {
                    tf.setStyle(fieldStyle +
                            "-fx-border-color: #3b82f6;" +
                            "-fx-background-color: #ffffff;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(59,130,246,0.2), 8, 0, 0, 0);");
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
                "-fx-text-fill: #ef4444;" +
                "-fx-font-size: 13px;" +
                "-fx-background-color: #fef2f2;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #fca5a5;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 8 12;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setWrapText(true);
        lbl.setVisible(false);
        lbl.setManaged(false);
        lbl.visibleProperty().addListener((obs, o, v) -> lbl.setManaged(v));
        return lbl;
    }

    private Label buildSuccessLabel(Label lbl) {
        lbl.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-size: 13px;" +
                "-fx-background-color: #f0fdf4;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #86efac;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 8 12;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setWrapText(true);
        lbl.setVisible(false);
        lbl.setManaged(false);
        lbl.visibleProperty().addListener((obs, o, v) -> lbl.setManaged(v));
        return lbl;
    }

    private Button buildPrimaryButton(Button btn, String text) {
        btn.setText(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(48);
        btn.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");
        
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #1d4ed8;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-font-family: 'Segoe UI', sans-serif;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(37,99,235,0.4), 10, 0, 0, 4);"));
        
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-font-family: 'Segoe UI', sans-serif;"));
        return btn;
    }

    private Button buildSuccessButton(Button btn, String text) {
        btn.setText(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(48);
        btn.setStyle(
                "-fx-background-color: #16a34a;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-font-family: 'Segoe UI', sans-serif;");
        
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #15803d;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-font-family: 'Segoe UI', sans-serif;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(22,163,74,0.4), 10, 0, 0, 4);"));
        
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #16a34a;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-font-family: 'Segoe UI', sans-serif;"));
        return btn;
    }
}