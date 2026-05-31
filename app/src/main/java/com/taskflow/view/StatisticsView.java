package com.taskflow.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

/**
 * Programmatic JavaFX replacement for statistics.fxml
 * Analytics dashboard with KPI cards and charts
 */
public class StatisticsView extends BorderPane {

    // Sidebar
    public final Button btnBack = new Button("📋  Dashboard");

    // Topbar
    public final Label pageTitle = new Label("Statistik");
    public final ComboBox<String> projectFilter = new ComboBox<>();
    public final Button backBtn = new Button("← Kembali");

    // KPI labels
    public final Label kpiTotal = new Label("0");
    public final Label kpiDone = new Label("0");
    public final Label kpiInProgress = new Label("0");
    public final Label kpiOverdue = new Label("0");

    // Charts
    public final PieChart pieChart = new PieChart();
    public final CategoryAxis barXAxis = new CategoryAxis();
    public final NumberAxis barYAxis = new NumberAxis();
    public final StackedBarChart<String, Number> barChart = new StackedBarChart<>(barXAxis, barYAxis);

    public final PieChart personalPieChart = new PieChart();
    public final CategoryAxis personalBarXAxis = new CategoryAxis();
    public final NumberAxis personalBarYAxis = new NumberAxis();
    public final BarChart<String, Number> personalBarChart = new BarChart<>(personalBarXAxis, personalBarYAxis);

    private static final String FONT = "'Segoe UI', 'Helvetica Neue', sans-serif;";
    private static final String SIDEBAR_BG = "#0f172a";

    public StatisticsView() {
        buildUI();
    }

    private void buildUI() {
        setStyle("-fx-background-color: #f1f5f9;");
        setPrefSize(1150, 700);
        setLeft(buildSidebar());
        setCenter(buildContent());
    }

    // ===================== SIDEBAR =====================

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.setStyle(
            "-fx-background-color: " + SIDEBAR_BG + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 3, 0);"
        );

        VBox header = new VBox(6);
        header.setPadding(new Insets(28, 20, 20, 20));
        header.setStyle("-fx-background-color: #1e293b;");

        Label logo = new Label("✅ TaskFlow");
        logo.setStyle(
            "-fx-font-size: 17px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-font-family: " + FONT
        );

        Label subtitle = new Label("Analitik & Statistik");
        subtitle.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #64748b;" +
            "-fx-font-family: " + FONT
        );

        header.getChildren().addAll(logo, subtitle);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1e293b;");
        VBox.setMargin(sep, new Insets(4, 0, 4, 0));

        Label navLabel = new Label("NAVIGASI");
        navLabel.setStyle(
            "-fx-font-size: 9.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #475569;" +
            "-fx-padding: 12 20 4 20;" +
            "-fx-font-family: " + FONT
        );

        // Dashboard nav btn
        btnBack.setMaxWidth(Double.MAX_VALUE);
        btnBack.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #94a3b8;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 11 16;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-font-family: " + FONT
        );
        btnBack.setOnMouseEntered(e -> btnBack.setStyle(
            "-fx-background-color: rgba(255,255,255,0.06);" +
            "-fx-text-fill: #e2e8f0;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 11 16;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-font-family: " + FONT
        ));
        btnBack.setOnMouseExited(e -> btnBack.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #94a3b8;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 11 16;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-font-family: " + FONT
        ));

        // Statistics btn (active)
        Button statsBtn = new Button("📊  Statistik");
        statsBtn.setMaxWidth(Double.MAX_VALUE);
        statsBtn.setStyle(
            "-fx-background-color: rgba(59,130,246,0.18);" +
            "-fx-text-fill: #60a5fa;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 11 16;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: default;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-border-color: #3b82f6;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 0 0 0 3;" +
            "-fx-font-family: " + FONT
        );

        HBox navBackWrap = new HBox(btnBack);
        navBackWrap.setPadding(new Insets(2, 12, 2, 12));
        HBox.setHgrow(btnBack, Priority.ALWAYS);

        HBox navStatsWrap = new HBox(statsBtn);
        navStatsWrap.setPadding(new Insets(2, 12, 2, 12));
        HBox.setHgrow(statsBtn, Priority.ALWAYS);

        sidebar.getChildren().addAll(header, sep, navLabel, navBackWrap, navStatsWrap);
        return sidebar;
    }

    // ===================== MAIN CONTENT =====================

    private VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: #f1f5f9;");
        VBox.setVgrow(content, Priority.ALWAYS);

        content.getChildren().addAll(buildTopbar(), buildScrollContent());
        VBox.setVgrow(buildScrollContent(), Priority.ALWAYS);

        // Need to rebuild cleanly
        content.getChildren().clear();

        HBox topbar = buildTopbar();
        ScrollPane scroll = buildScrollContent();
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(topbar, scroll);
        return content;
    }

    private HBox buildTopbar() {
        HBox topbar = new HBox(14);
        topbar.setPadding(new Insets(16, 24, 16, 24));
        topbar.setAlignment(Pos.CENTER_LEFT);
        topbar.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: transparent transparent #e2e8f0 transparent;" +
            "-fx-border-width: 0 0 1 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 8, 0, 0, 2);"
        );

        backBtn.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-text-fill: #475569;" +
            "-fx-font-size: 12.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 8;" +
            "-fx-font-family: " + FONT
        );
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(
            "-fx-background-color: #e2e8f0;" +
            "-fx-text-fill: #1e293b;" +
            "-fx-font-size: 12.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: #cbd5e1;" +
            "-fx-border-radius: 8;" +
            "-fx-font-family: " + FONT
        ));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-text-fill: #475569;" +
            "-fx-font-size: 12.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 8;" +
            "-fx-font-family: " + FONT
        ));

        pageTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #0f172a;" +
            "-fx-font-family: " + FONT
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label filterLbl = new Label("Filter Proyek:");
        filterLbl.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #64748b;" +
            "-fx-font-family: " + FONT
        );

        projectFilter.setPrefWidth(170);
        projectFilter.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 12.5px;" +
            "-fx-font-family: " + FONT
        );

        topbar.getChildren().addAll(backBtn, pageTitle, spacer, filterLbl, projectFilter);
        return topbar;
    }

    private ScrollPane buildScrollContent() {
        VBox inner = new VBox(20);
        inner.setPadding(new Insets(24));
        inner.setStyle("-fx-background-color: #f1f5f9;");

        // KPI row
        inner.getChildren().add(buildKpiRow());

        // Group tasks section
        inner.getChildren().add(buildSectionDivider("📌  Tugas Kelompok", "#3b82f6"));
        inner.getChildren().add(buildGroupCharts());

        // Personal tasks section
        inner.getChildren().add(buildSectionDivider("📝  Tugas Mandiri", "#10b981"));
        inner.getChildren().add(buildPersonalCharts());

        ScrollPane scroll = new ScrollPane(inner);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
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

        for (javafx.scene.Node child : row.getChildren()) {
            HBox.setHgrow(child, Priority.ALWAYS);
        }

        return row;
    }

    private VBox buildKpiCard(String label, Label valueLabel, String accentColor, String bgColor) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20, 16, 20, 16));
        card.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + accentColor + "22;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);"
        );

        // Colored top accent bar
        Rectangle bar = new Rectangle(40, 4);
        bar.setArcWidth(4);
        bar.setArcHeight(4);
        bar.setStyle("-fx-fill: " + accentColor + ";");

        Label lbl = new Label(label);
        lbl.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #64748b;" +
            "-fx-font-family: " + FONT
        );

        valueLabel.setStyle(
            "-fx-font-size: 36px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + accentColor + ";" +
            "-fx-font-family: " + FONT
        );

        card.getChildren().addAll(bar, lbl, valueLabel);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private HBox buildGroupCharts() {
        HBox row = new HBox(16);
        row.setMinHeight(300);

        // Pie chart card
        VBox pieCard = buildChartCard("Status Tugas Kelompok");
        pieChart.setAnimated(true);
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);
        pieChart.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(pieChart, Priority.ALWAYS);
        pieCard.getChildren().add(pieChart);

        // Bar chart card
        VBox barCard = buildChartCard("Analisis Beban Kerja Tim");
        barXAxis.setLabel("Anggota");
        barYAxis.setLabel("Jumlah Tugas");
        barChart.setAnimated(true);
        barChart.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(barChart, Priority.ALWAYS);
        barCard.getChildren().add(barChart);

        HBox.setHgrow(pieCard, Priority.ALWAYS);
        HBox.setHgrow(barCard, Priority.ALWAYS);
        row.getChildren().addAll(pieCard, barCard);
        return row;
    }

    private HBox buildPersonalCharts() {
        HBox row = new HBox(16);
        row.setMinHeight(300);

        VBox pieCard = buildChartCard("Status Tugas Mandiri");
        personalPieChart.setAnimated(true);
        personalPieChart.setLegendVisible(true);
        personalPieChart.setLabelsVisible(true);
        personalPieChart.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(personalPieChart, Priority.ALWAYS);
        pieCard.getChildren().add(personalPieChart);

        VBox barCard = buildChartCard("Tugas Mandiri per Kategori");
        personalBarXAxis.setLabel("Kategori");
        personalBarYAxis.setLabel("Jumlah Tugas");
        personalBarChart.setAnimated(true);
        personalBarChart.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(personalBarChart, Priority.ALWAYS);
        barCard.getChildren().add(personalBarChart);

        HBox.setHgrow(pieCard, Priority.ALWAYS);
        HBox.setHgrow(barCard, Priority.ALWAYS);
        row.getChildren().addAll(pieCard, barCard);
        return row;
    }

    private VBox buildChartCard(String title) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16, 16, 16, 16));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"
        );

        Label lbl = new Label(title);
        lbl.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #1e293b;" +
            "-fx-font-family: " + FONT
        );
        card.getChildren().add(lbl);
        return card;
    }

    private HBox buildSectionDivider(String text, String color) {
        HBox box = new HBox(12);
        box.setAlignment(Pos.CENTER_LEFT);

        Rectangle bar = new Rectangle(4, 20);
        bar.setArcWidth(4);
        bar.setArcHeight(4);
        bar.setStyle("-fx-fill: " + color + ";");

        Label lbl = new Label(text);
        lbl.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #0f172a;" +
            "-fx-font-family: " + FONT
        );

        Region line = new Region();
        line.setPrefHeight(1);
        line.setStyle("-fx-background-color: #e2e8f0;");
        HBox.setHgrow(line, Priority.ALWAYS);
        HBox.setMargin(line, new Insets(0, 0, 0, 8));

        box.getChildren().addAll(bar, lbl, line);
        return box;
    }
}
