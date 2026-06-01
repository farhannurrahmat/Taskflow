package com.taskflow.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DashboardView extends BorderPane {

    // Komponen Sidebar
    public final Label welcomeLabel = new Label();
    public final Button btnDashboard = new Button("Dashboard");
    public final Button btnStatistics = new Button("Statistik");
    public final Button btnLogout = new Button("Keluar");

    // Komponen Topbar
    public final Label pageTitleLabel = new Label("Fokus Hari Ini");
    public final Label pageSubtitleLabel = new Label("Fokus pada tugas yang paling mendesak hari ini.");
    public final Button btnQuickAdd = new Button("＋ Tugas Baru");
    public final ProgressBar dailyProgressBar = new ProgressBar(0);
    public final Label progressLabel = new Label("0% Selesai");

    // Kontainer Kartu Tugas
    public final FlowPane urgentTasksContainer = new FlowPane(15, 15);
    public final FlowPane upcomingTasksContainer = new FlowPane(15, 15);
    public final FlowPane completedTasksContainer = new FlowPane(15, 15); // KOTAK BARU UNTUK TUGAS SELESAI

    private final String FONT = "Segoe UI, sans-serif";

    public DashboardView() {
        setLeft(buildSidebar());
        
        VBox mainArea = new VBox();
        mainArea.setStyle("-fx-background-color: #f8fafc;");
        mainArea.getChildren().addAll(buildTopbar(), buildContent());
        
        setCenter(mainArea);
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.setStyle("-fx-background-color: #0f172a;");

        // App Branding
        VBox brandBox = new VBox(2);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        
        Label appName = new Label("TaskFlow");
        appName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: " + FONT);
        
        welcomeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8; -fx-font-family: " + FONT);
        
        brandBox.getChildren().addAll(appName, welcomeLabel);

        // Separator
        Region sep = new Region();
        sep.setMinHeight(1);
        sep.setStyle("-fx-background-color: #1e293b;");
        VBox.setMargin(sep, new Insets(10, 0, 10, 0));

        // Menu Title
        Label menuLabel = new Label("WORKSPACE");
        menuLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748b; -fx-font-family: " + FONT);

        // Buttons
        styleSidebarBtnActive(btnDashboard);
        styleSidebarBtnInactive(btnStatistics);
        styleSidebarBtnInactive(btnLogout);
        btnLogout.setStyle(btnLogout.getStyle() + "; -fx-text-fill: #ef4444;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brandBox, sep, menuLabel, btnDashboard, btnStatistics, spacer, btnLogout);
        return sidebar;
    }

    private void styleSidebarBtnActive(Button btn) {
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10 16; -fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: #3b82f6; -fx-border-width: 0 0 0 3; -fx-border-radius: 8; -fx-font-family: " + FONT);
    }

    private void styleSidebarBtnInactive(Button btn) {
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-padding: 10 16; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-family: " + FONT);
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

        // Progress Bar
        VBox progressBox = new VBox(4);
        progressBox.setAlignment(Pos.CENTER_RIGHT);
        progressLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #10b981; -fx-font-family: " + FONT);
        dailyProgressBar.setPrefWidth(150);
        dailyProgressBar.setPrefHeight(10);
        dailyProgressBar.setStyle("-fx-accent: #10b981; -fx-control-inner-background: #f1f5f9; -fx-background-radius: 10;");
        progressBox.getChildren().addAll(progressLabel, dailyProgressBar);

        btnQuickAdd.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-family: " + FONT);

        topbar.getChildren().addAll(titleBox, spacer, progressBox, btnQuickAdd);
        return topbar;
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));
        
        // 1. Section Urgent
        VBox urgentSection = new VBox(15);
        urgentSection.getChildren().addAll(urgentTasksContainer);

        // 2. Section Mendatang
        VBox upcomingSection = new VBox(15);
        Label upTitle = new Label("Tugas Mendatang");
        upTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-family: " + FONT);
        upcomingSection.getChildren().addAll(upTitle, upcomingTasksContainer);

        // 3. Section Selesai
        VBox completedSection = new VBox(15);
        Label compTitle = new Label("Tugas Selesai 🎉");
        compTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-family: " + FONT);
        completedSection.getChildren().addAll(compTitle, completedTasksContainer);

        content.getChildren().addAll(urgentSection, upcomingSection, completedSection);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: #f8fafc;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    public VBox createTaskCard(String title, String category, String deadline, String priority, String status) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-cursor: hand;");
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-cursor: hand;"));

        // Top Row: Category & Priority
        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        Label catLbl = new Label(category);
        catLbl.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 20;");
        
        Label prioLbl = new Label(priority);
        String prioColor = priority.equals("High") ? "#fee2e2" : priority.equals("Medium") ? "#fef3c7" : "#d1fae5";
        String prioText = priority.equals("High") ? "#ef4444" : priority.equals("Medium") ? "#f59e0b" : "#10b981";
        prioLbl.setStyle("-fx-background-color: " + prioColor + "; -fx-text-fill: " + prioText + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 20;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label optLbl = new Label("⋮");
        optLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold;");

        topRow.getChildren().addAll(catLbl, prioLbl, spacer, optLbl);

        // Title
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        titleLbl.setWrapText(true);

        // Bottom Row: Deadline & Status
        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        Label dateLbl = new Label("📅 " + deadline);
        dateLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        Label statLbl = new Label(status);
        String statColor = status.equals("Done") ? "#10b981" : status.equals("In Progress") ? "#3b82f6" : "#64748b";
        statLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + statColor + ";");

        bottomRow.getChildren().addAll(dateLbl, spacer2, statLbl);
        card.getChildren().addAll(topRow, titleLbl, bottomRow);

        return card;
    }
}