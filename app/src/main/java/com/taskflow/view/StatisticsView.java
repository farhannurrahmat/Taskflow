package com.taskflow.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

public class StatisticsView extends BorderPane {

    public final Label welcomeLabel = new Label();
    public final Button btnDashboard = new Button("Dashboard");
    public final Button btnStatistics = new Button("Statistik");
    public final Button btnLogout = new Button("Keluar");

    public final Label pageTitleLabel = new Label("Statistik Produktivitas");
    public final Label pageSubtitleLabel = new Label("Pantau performa dan penyelesaian tugas Anda.");

    public final PieChart statusPieChart = new PieChart();
    public final BarChart<String, Number> categoryBarChart;
    public final CategoryAxis xAxis = new CategoryAxis();
    public final NumberAxis yAxis = new NumberAxis();
    
    public final Label totalTaskLabel = new Label("0");
    public final Label inProgressTaskLabel = new Label("0"); 
    public final Label completedTaskLabel = new Label("0");
    public final Label overdueTaskLabel = new Label("0");

    private final String FONT = "Segoe UI, sans-serif";

    public StatisticsView() {
        categoryBarChart = new BarChart<>(xAxis, yAxis);
        categoryBarChart.setTitle("Distribusi Tugas per Kategori");
        xAxis.setLabel("Kategori");
        yAxis.setLabel("Jumlah Tugas");
        
        statusPieChart.setTitle("Rasio Status Tugas");
        statusPieChart.setLabelsVisible(true);
        statusPieChart.setLegendVisible(true);

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

        VBox brandBox = new VBox(2);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        
        Label appName = new Label("TaskFlow");
        appName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: " + FONT);
        
        welcomeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8; -fx-font-family: " + FONT);
        
        brandBox.getChildren().addAll(appName, welcomeLabel);

        Region sep = new Region();
        sep.setMinHeight(1);
        sep.setStyle("-fx-background-color: #1e293b;");
        VBox.setMargin(sep, new Insets(10, 0, 10, 0));

        Label menuLabel = new Label("WORKSPACE");
        menuLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748b; -fx-font-family: " + FONT);

        styleSidebarBtnInactive(btnDashboard); 
        styleSidebarBtnActive(btnStatistics); 
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

        topbar.getChildren().addAll(titleBox);
        return topbar;
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        HBox summaryCards = new HBox(15);
        summaryCards.getChildren().addAll(
            createSummaryCard("Total Tugas", totalTaskLabel, "#3b82f6"),
            createSummaryCard("Sedang Berjalan", inProgressTaskLabel, "#f59e0b"),
            createSummaryCard("Selesai", completedTaskLabel, "#10b981"),
            createSummaryCard("Terlambat", overdueTaskLabel, "#ef4444")
        );

        HBox chartsArea = new HBox(20);
        
        VBox pieBox = new VBox(statusPieChart);
        pieBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");
        HBox.setHgrow(pieBox, Priority.ALWAYS);
        
        VBox barBox = new VBox(categoryBarChart);
        barBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");
        HBox.setHgrow(barBox, Priority.ALWAYS);

        chartsArea.getChildren().addAll(pieBox, barBox);
        
        content.getChildren().addAll(summaryCards, chartsArea);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: #f8fafc;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        
        return scroll;
    }

private VBox createSummaryCard(String title, Label valueLabel, String colorHex) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(20));
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);
        
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4); -fx-border-color: " + colorHex + "; -fx-border-width: 0 0 0 4; -fx-border-radius: 12;");
        
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-font-family: " + FONT);
        
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-font-family: " + FONT);
        
        card.getChildren().addAll(titleLbl, valueLabel);
        return card;
    }
}