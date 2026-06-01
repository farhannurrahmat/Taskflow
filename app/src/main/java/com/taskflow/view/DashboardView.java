package com.taskflow.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

public class DashboardView extends BorderPane {

    
    public final Label welcomeLabel = new Label("Farhan Nurrahmat");
    public final Button btnDashboard = new Button();
    public final Button btnStatistics = new Button();
    public final Button btnLogout = new Button();


    public final Label pageTitleLabel = new Label("Today's Focus");
    public final Label pageSubtitleLabel = new Label("Fokus pada tugas yang paling mendesak hari ini.");
    public final Button btnQuickAdd = new Button("＋ Tugas Baru");
        public final ProgressBar dailyProgressBar = new ProgressBar(0);
    public final Label progressLabel = new Label("0% Selesai");

    public final FlowPane urgentTasksContainer = new FlowPane();
    public final FlowPane upcomingTasksContainer = new FlowPane();

    private static final String FONT = "'Segoe UI', 'Helvetica Neue', sans-serif;";
    private static final String SIDEBAR_BG = "#0f172a";
    private static final String CONTENT_BG = "#f8fafc";

    public DashboardView() {
        buildUI();
        
       
        urgentTasksContainer.getChildren().addAll(
            createTaskCard("Final Project PBO JavaFX", "Kuliah", "Besok, 23:59", "High", "To Do"),
            createTaskCard("Revisi Proposal PKM-RE PEDAS", "Riset/PKM", "Hari ini, 18:00", "High", "In Progress")
        );
        upcomingTasksContainer.getChildren().addAll(
            createTaskCard("Workout Pull Day 15-50-25", "Pribadi", "3 Jun 2026", "Medium", "To Do"),
            createTaskCard("Analisis Market XAU/USD", "Bisnis", "5 Jun 2026", "Low", "To Do")
        );
    }

    private void buildUI() {
        setStyle("-fx-background-color: " + CONTENT_BG + ";");
        setPrefSize(1180, 740);

        setLeft(buildSidebar());

        VBox center = new VBox(0);
        center.setStyle("-fx-background-color: " + CONTENT_BG + ";");
        
        center.getChildren().addAll(buildTopbar(), buildMainContent());
        setCenter(center);
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_BG + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 3, 0);");

        VBox header = new VBox(8);
        header.setPadding(new Insets(28, 20, 20, 20));
        header.setStyle("-fx-background-color: #1e293b;");

        HBox avatarRow = new HBox(12);
        avatarRow.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = buildAvatar();
        VBox nameBox = new VBox(2);
        Label appLabel = new Label("Taskflow");
        appLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: " + FONT);
        welcomeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8; -fx-font-family: " + FONT);
        nameBox.getChildren().addAll(appLabel, welcomeLabel);
        
        avatarRow.getChildren().addAll(avatar, nameBox);
        header.getChildren().add(avatarRow);

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #1e293b;");
        VBox.setMargin(sep1, new Insets(8, 0, 8, 0));

        Label menuLabel = new Label("WORKSPACE");
        menuLabel.setStyle("-fx-font-size: 9.5px; -fx-font-weight: bold; -fx-text-fill: #475569; -fx-padding: 12 20 4 20; -fx-letter-spacing: 1; -fx-font-family: " + FONT);

        btnDashboard.setText("🏠  Dashboard");
        btnStatistics.setText("📊  Statistik");

        styleSidebarBtnActive(btnDashboard);
        styleSidebarBtn(btnStatistics);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        btnLogout.setText("🚪  Keluar");
        styleSidebarBtnDanger(btnLogout);
        VBox.setMargin(btnLogout, new Insets(0, 12, 16, 12));

        sidebar.getChildren().addAll(
            header, sep1, menuLabel,
            wrapNav(btnDashboard), wrapNav(btnStatistics),
            spacer, btnLogout
        );

        return sidebar;
    }

    private StackPane buildAvatar() {
        Circle circle = new Circle(20);
        circle.setFill(Color.web("#3b64f6fc", 0.22));
        Label icon = new Label("👤");
        icon.setStyle("-fx-font-size: 33px;");
        return new StackPane(circle, icon);
    }

    private HBox wrapNav(Button btn) {
        HBox box = new HBox(btn);
        box.setPadding(new Insets(2, 12, 2, 12));
        HBox.setHgrow(btn, Priority.ALWAYS);
        btn.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    public void styleSidebarBtnActive(Button btn) {
        btn.setStyle("-fx-background-color: rgba(59,130,246,0.18); -fx-text-fill: #60a5fa; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 11 16; -fx-background-radius: 10; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-border-color: #3b82f6; -fx-border-radius: 10; -fx-border-width: 0 0 0 3; -fx-font-family: " + FONT);
    }

    public void styleSidebarBtn(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-padding: 11 16; -fx-background-radius: 10; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-font-family: " + FONT);
    }

    private void styleSidebarBtnDanger(Button btn) {
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #f87171; -fx-font-size: 13px; -fx-padding: 11 16; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-font-family: " + FONT);
    }

    private HBox buildTopbar() {
        HBox topbar = new HBox(15);
        topbar.setPadding(new Insets(20, 30, 20, 30));
        topbar.setAlignment(Pos.CENTER_LEFT);
        topbar.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 0 0 1 0;");

        VBox titleBox = new VBox(2);
        pageTitleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-font-family: " + FONT);
        pageSubtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-family: " + FONT);
        titleBox.getChildren().addAll(pageTitleLabel, pageSubtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- TAMBAHAN PROGRESS BAR UI ---
        VBox progressBox = new VBox(4);
        progressBox.setAlignment(Pos.CENTER_RIGHT);
        
        progressLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #10b981; -fx-font-family: " + FONT);
        
        dailyProgressBar.setPrefWidth(150);
        dailyProgressBar.setPrefHeight(10);
        dailyProgressBar.setStyle("-fx-accent: #10b981; -fx-control-inner-background: #f1f5f9; -fx-background-radius: 10;");

        progressBox.getChildren().addAll(progressLabel, dailyProgressBar);
        // --------------------------------

        btnQuickAdd.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-family: " + FONT);

        topbar.getChildren().addAll(titleBox, spacer, progressBox, btnQuickAdd);
        return topbar;
    }

    private ScrollPane buildMainContent() {
        VBox contentBox = new VBox(30);
        contentBox.setPadding(new Insets(30));
        contentBox.setStyle("-fx-background-color: transparent;");

        urgentTasksContainer.setHgap(20);
        urgentTasksContainer.setVgap(20);

        upcomingTasksContainer.setHgap(20);
        upcomingTasksContainer.setVgap(20);

        Label upcomingLabel = new Label("Tugas Mendatang");
        upcomingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-family: " + FONT);

        contentBox.getChildren().addAll(urgentTasksContainer, upcomingLabel, upcomingTasksContainer);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return scrollPane;
    }

    public VBox createTaskCard(String title, String category, String deadline, String priority, String status) {
        VBox card = new VBox(12);
        card.setPrefWidth(320);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 10, 0, 0, 4);");

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        Label catBadge = new Label(category);
        catBadge.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 20; -fx-font-family: " + FONT);
        
        String prioColor = priority.equals("High") ? "#fee2e2" : priority.equals("Medium") ? "#fef3c7" : "#dcfce7";
        String prioText = priority.equals("High") ? "#ef4444" : priority.equals("Medium") ? "#d97706" : "#16a34a";
        Label prioBadge = new Label(priority);
        prioBadge.setStyle("-fx-background-color: " + prioColor + "; -fx-text-fill: " + prioText + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 20; -fx-font-family: " + FONT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnOptions = new Button("⋮");
        btnOptions.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 16px; -fx-cursor: hand; -fx-padding: 0;");
        
        topRow.getChildren().addAll(catBadge, prioBadge, spacer, btnOptions);

        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-font-family: " + FONT);

        HBox bottomRow = new HBox(10);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        Label dateIcon = new Label("📅 " + deadline);
        dateIcon.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-family: " + FONT);
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #3b82f6; -fx-font-family: " + FONT);

        bottomRow.getChildren().addAll(dateIcon, spacer2, statusLabel);

        card.getChildren().addAll(topRow, titleLabel, bottomRow);
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle().replace("#e2e8f0", "#93c5fd") + "-fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("#93c5fd", "#e2e8f0")));

        return card;
    }
}