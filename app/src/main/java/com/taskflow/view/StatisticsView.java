package com.taskflow.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

public class StatisticsView extends BorderPane {

    public final Button btnBack = new Button("📋  Dashboard");

    public final Label pageTitle = new Label("Statistik Produktivitas");
    public final Button backBtn = new Button("← Kembali");

    public final Label kpiTotal = new Label("0");
    public final Label kpiDone = new Label("0");
    public final Label kpiInProgress = new Label("0");
    public final Label kpiOverdue = new Label("0");

    public final PieChart statusPieChart = new PieChart();
    public final CategoryAxis categoryBarXAxis = new CategoryAxis();
    public final NumberAxis categoryBarYAxis = new NumberAxis();
    public final BarChart<String, Number> categoryBarChart = new BarChart<>(categoryBarXAxis, categoryBarYAxis);

    private static final String FONT = "'Segoe UI', 'Helvetica Neue', sans-serif;";
    private static final String SIDEBAR_BG = "#0f172a";

    public StatisticsView() {
        buildUI();
    }

    private void buildUI() {
        setStyle("-fx-background-color: #f1f5f9;");
        setPrefSize(1150, 700);
        setLeft(buildSidebar());
        
        VBox center = new VBox(0);
        center.getChildren().addAll(buildTopbar(), buildScrollContent());
        VBox.setVgrow(center.getChildren().get(1), Priority.ALWAYS);
        setCenter(center);
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_BG + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 3, 0);");

        VBox header = new VBox(6);
        header.setPadding(new Insets(28, 20, 20, 20));
        header.setStyle("-fx-background-color: #1e293b;");
        Label logo = new Label("Taskflow");
        logo.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: " + FONT);
        header.getChildren().add(logo);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1e293b;");
        VBox.setMargin(sep, new Insets(4, 0, 4, 0));

        Label navLabel = new Label("WORKSPACE");
        navLabel.setStyle("-fx-font-size: 9.5px; -fx-font-weight: bold; -fx-text-fill: #475569; -fx-padding: 12 20 4 20; -fx-font-family: " + FONT);

        btnBack.setMaxWidth(Double.MAX_VALUE);
        btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-padding: 11 16; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-font-family: " + FONT);

        Button statsBtn = new Button("📊  Statistik");
        statsBtn.setMaxWidth(Double.MAX_VALUE);
        statsBtn.setStyle("-fx-background-color: rgba(59,130,246,0.18); -fx-text-fill: #60a5fa; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 11 16; -fx-background-radius: 10; -fx-cursor: default; -fx-alignment: CENTER_LEFT; -fx-border-color: #3b82f6; -fx-border-width: 0 0 0 3; -fx-font-family: " + FONT);

        HBox navBackWrap = new HBox(btnBack);
        navBackWrap.setPadding(new Insets(2, 12, 2, 12));
        HBox.setHgrow(btnBack, Priority.ALWAYS);

        HBox navStatsWrap = new HBox(statsBtn);
        navStatsWrap.setPadding(new Insets(2, 12, 2, 12));
        HBox.setHgrow(statsBtn, Priority.ALWAYS);

        sidebar.getChildren().addAll(header, sep, navLabel, navBackWrap, navStatsWrap);
        return sidebar;
    }

    private HBox buildTopbar() {
        HBox topbar = new HBox(14);
        topbar.setPadding(new Insets(16, 24, 16, 24));
        topbar.setAlignment(Pos.CENTER_LEFT);
        topbar.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 0 0 1 0;");

        backBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-size: 12.5px; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-family: " + FONT);
        pageTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-font-family: " + FONT);

        topbar.getChildren().addAll(backBtn, pageTitle);
        return topbar;
    }

    private ScrollPane buildScrollContent() {
        VBox inner = new VBox(20);
        inner.setPadding(new Insets(24));
        inner.setStyle("-fx-background-color: #f1f5f9;");

        inner.getChildren().add(buildKpiRow());
        inner.getChildren().add(buildChartsRow());

        ScrollPane scroll = new ScrollPane(inner);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    private HBox buildKpiRow() {
        HBox row = new HBox(16);
        row.getChildren().addAll(
            buildKpiCard("📋  Total Tugas", kpiTotal, "#3b82f6", "#eff6ff"),
            buildKpiCard("✅  Selesai", kpiDone, "#10b981", "#f0fdf4"),
            buildKpiCard("🔄  Dikerjakan", kpiInProgress, "#f59e0b", "#fffbeb"),
            buildKpiCard("⚠  Terlambat", kpiOverdue, "#ef4444", "#fff1f2")
        );
        for (javafx.scene.Node child : row.getChildren()) HBox.setHgrow(child, Priority.ALWAYS);
        return row;
    }

    private VBox buildKpiCard(String label, Label valueLabel, String accentColor, String bgColor) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20, 16, 20, 16));
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 14; -fx-border-color: " + accentColor + "22; -fx-border-radius: 14; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Rectangle bar = new Rectangle(40, 4);
        bar.setArcWidth(4); bar.setArcHeight(4);
        bar.setStyle("-fx-fill: " + accentColor + ";");

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #64748b; -fx-font-family: " + FONT);

        valueLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + accentColor + "; -fx-font-family: " + FONT);

        card.getChildren().addAll(bar, lbl, valueLabel);
        return card;
    }

    private HBox buildChartsRow() {
        HBox row = new HBox(16);
        row.setMinHeight(350);

        VBox pieCard = buildChartCard("Distribusi Status Tugas");
        statusPieChart.setAnimated(true);
        statusPieChart.setLegendVisible(true);
        statusPieChart.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(statusPieChart, Priority.ALWAYS);
        pieCard.getChildren().add(statusPieChart);

        VBox barCard = buildChartCard("Tugas Berdasarkan Kategori");
        categoryBarXAxis.setLabel("Kategori");
        categoryBarYAxis.setLabel("Jumlah Tugas");
        categoryBarChart.setAnimated(true);
        categoryBarChart.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(categoryBarChart, Priority.ALWAYS);
        barCard.getChildren().add(categoryBarChart);

        HBox.setHgrow(pieCard, Priority.ALWAYS);
        HBox.setHgrow(barCard, Priority.ALWAYS);
        row.getChildren().addAll(pieCard, barCard);
        return row;
    }

    private VBox buildChartCard(String title) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #e2e8f0; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);");
        
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-font-family: " + FONT);
        card.getChildren().add(lbl);
        return card;
    }
}