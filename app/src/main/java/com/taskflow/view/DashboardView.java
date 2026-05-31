package com.taskflow.view;

import com.taskflow.model.PersonalTask;
import com.taskflow.model.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

/**
 * Programmatic JavaFX replacement for dashboard.fxml
 * Professional sidebar layout with modern card-style UI
 */
public class DashboardView extends BorderPane {

    // ---- Sidebar ----
    public final Label welcomeLabel = new Label();
    public final Button btnNavGroup = new Button();
    public final Button btnNavPersonal = new Button();
    public final Button btnStatistics = new Button();
    public final Button btnLogout = new Button();

    // ---- Topbar ----
    public final Label pageTitleLabel = new Label("Dashboard");
    public final Label pageSubtitleLabel = new Label("Kelola semua tugas kamu di sini");

    // ---- Reminder Banner ----
    public final VBox reminderBanner = new VBox();
    public final Label reminderLabel = new Label();
    public final Button dismissReminderBtn = new Button("✕ Tutup");

    // ---- Group Section ----
    public final VBox groupSection = new VBox();
    public final TableView<Task> taskTable = new TableView<>();
    public final TableColumn<Task, String> colId = new TableColumn<>("#");
    public final TableColumn<Task, String> colTitle = new TableColumn<>("Judul Tugas");
    public final TableColumn<Task, String> colProject = new TableColumn<>("Proyek / Kelompok");
    public final TableColumn<Task, String> colAssigned = new TableColumn<>("Ditugaskan Ke");
    public final TableColumn<Task, String> colPriority = new TableColumn<>("Prioritas");
    public final TableColumn<Task, String> colStatus = new TableColumn<>("Status");
    public final TableColumn<Task, java.time.LocalDate> colDeadline = new TableColumn<>("Deadline");
    public final Button btnAdd = new Button("＋ Tambah");
    public final Button btnEdit = new Button("✏ Edit");
    public final Button btnChangeStatus = new Button("⇄ Status");
    public final Button btnDelete = new Button("⌫ Hapus");
    public final TextField searchField = new TextField();
    public final ComboBox<String> filterStatus = new ComboBox<>();
    public final Label statusBar = new Label();

    // ---- Personal Section ----
    public final VBox personalSection = new VBox();
    public final TableView<PersonalTask> personalTable = new TableView<>();
    public final TableColumn<PersonalTask, String> pColId = new TableColumn<>("#");
    public final TableColumn<PersonalTask, String> pColTitle = new TableColumn<>("Judul Tugas Mandiri");
    public final TableColumn<PersonalTask, String> pColCategory = new TableColumn<>("Kategori");
    public final TableColumn<PersonalTask, String> pColPriority = new TableColumn<>("Prioritas");
    public final TableColumn<PersonalTask, String> pColStatus = new TableColumn<>("Status");
    public final TableColumn<PersonalTask, java.time.LocalDate> pColDeadline = new TableColumn<>("Deadline");
    public final Button pBtnAdd = new Button("＋ Tambah");
    public final Button pBtnEdit = new Button("✏ Edit");
    public final Button pBtnChangeStatus = new Button("⇄ Status");
    public final Button pBtnDelete = new Button("⌫ Hapus");
    public final TextField pSearchField = new TextField();
    public final ComboBox<String> pFilterStatus = new ComboBox<>();
    public final Label pStatusBar = new Label();

    // Style constants
    private static final String FONT = "'Segoe UI', 'Helvetica Neue', sans-serif;";
    private static final String SIDEBAR_BG = "#0f172a";
    private static final String ACCENT = "#3b82f6";
    private static final String CONTENT_BG = "#f1f5f9";

    public DashboardView() {
        buildUI();
    }

    private void buildUI() {
        setStyle("-fx-background-color: " + CONTENT_BG + ";");
        setPrefSize(1180, 740);

        setLeft(buildSidebar());

        VBox center = new VBox(0);
        center.setStyle("-fx-background-color: " + CONTENT_BG + ";");
        VBox.setVgrow(center, Priority.ALWAYS);

        buildTopbar(center);
        buildReminderBanner();
        buildGroupSection();
        buildPersonalSection();

        center.getChildren().addAll(buildTopbarNode(), reminderBanner, groupSection, personalSection);
        VBox.setVgrow(groupSection, Priority.ALWAYS);
        VBox.setVgrow(personalSection, Priority.ALWAYS);

        setCenter(center);
    }

    // ===================== SIDEBAR =====================

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle(
            "-fx-background-color: " + SIDEBAR_BG + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 3, 0);"
        );

        // Header
        VBox header = new VBox(8);
        header.setPadding(new Insets(28, 20, 20, 20));
        header.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 0;");

        // Avatar + name row
        HBox avatarRow = new HBox(12);
        avatarRow.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = buildAvatar();

        VBox nameBox = new VBox(2);
        Label appLabel = new Label("✅ TaskFlow");
        appLabel.setStyle(
            "-fx-font-size: 17px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-font-family: " + FONT
        );
        welcomeLabel.setStyle(
            "-fx-font-size: 11.5px;" +
            "-fx-text-fill: #94a3b8;" +
            "-fx-font-family: " + FONT
        );
        welcomeLabel.setWrapText(true);

        Label roleBadge = new Label("Mahasiswa");
        roleBadge.setStyle(
            "-fx-font-size: 9.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3b82f6;" +
            "-fx-background-color: rgba(59,130,246,0.15);" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 2 8;" +
            "-fx-font-family: " + FONT
        );

        nameBox.getChildren().addAll(appLabel, welcomeLabel, roleBadge);
        avatarRow.getChildren().addAll(avatar, nameBox);
        header.getChildren().add(avatarRow);

        // Divider
        Separator sep1 = styledSeparator();

        // Nav label
        Label menuLabel = buildSectionLabel("MENU UTAMA");

        // Nav buttons
        btnNavGroup.setText("👥  Tugas Kelompok");
        btnNavPersonal.setText("📝  Tugas Mandiri");
        btnStatistics.setText("📊  Statistik");

        styleSidebarBtnActive(btnNavGroup);
        styleSidebarBtn(btnNavPersonal);
        styleSidebarBtn(btnStatistics);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Separator sep2 = styledSeparator();

        btnLogout.setText("🚪  Keluar");
        styleSidebarBtnDanger(btnLogout);
        VBox.setMargin(btnLogout, new Insets(0, 12, 16, 12));

        sidebar.getChildren().addAll(
            header, sep1, menuLabel,
            wrapNav(btnNavGroup), wrapNav(btnNavPersonal), wrapNav(btnStatistics),
            spacer, sep2, btnLogout
        );

        return sidebar;
    }

    private StackPane buildAvatar() {
        Circle circle = new Circle(22);
        circle.setFill(Color.web(ACCENT, 0.22));
        circle.setStroke(Color.web(ACCENT, 0.5));
        circle.setStrokeWidth(1.5);

        Label icon = new Label("👤");
        icon.setStyle("-fx-font-size: 16px;");

        StackPane sp = new StackPane(circle, icon);
        sp.setPrefSize(44, 44);
        return sp;
    }

    private HBox wrapNav(Button btn) {
        HBox box = new HBox(btn);
        box.setPadding(new Insets(2, 12, 2, 12));
        HBox.setHgrow(btn, Priority.ALWAYS);
        btn.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    public void styleSidebarBtnActive(Button btn) {
        btn.setStyle(
            "-fx-background-color: rgba(59,130,246,0.18);" +
            "-fx-text-fill: #60a5fa;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 11 16;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-border-color: #3b82f6;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 0 0 0 3;" +
            "-fx-font-family: " + FONT
        );
    }

    public void styleSidebarBtn(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #94a3b8;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 11 16;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-font-family: " + FONT
        );
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("#60a5fa")) {
                btn.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.06);" +
                    "-fx-text-fill: #e2e8f0;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 11 16;" +
                    "-fx-background-radius: 10;" +
                    "-fx-cursor: hand;" +
                    "-fx-alignment: CENTER_LEFT;" +
                    "-fx-font-family: " + FONT
                );
            }
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("#60a5fa")) {
                styleSidebarBtn(btn);
            }
        });
    }

    private void styleSidebarBtnDanger(Button btn) {
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
            "-fx-background-color: rgba(239,68,68,0.12);" +
            "-fx-text-fill: #f87171;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 11 16;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-font-family: " + FONT
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: rgba(239,68,68,0.22);" +
            "-fx-text-fill: #fca5a5;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 11 16;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-font-family: " + FONT
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: rgba(239,68,68,0.12);" +
            "-fx-text-fill: #f87171;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 11 16;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-font-family: " + FONT
        ));
    }

    // ===================== TOPBAR =====================

    private void buildTopbar(VBox parent) {}

    private HBox buildTopbarNode() {
        HBox topbar = new HBox(12);
        topbar.setPadding(new Insets(18, 24, 18, 24));
        topbar.setAlignment(Pos.CENTER_LEFT);
        topbar.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: transparent transparent #e2e8f0 transparent;" +
            "-fx-border-width: 0 0 1 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 8, 0, 0, 2);"
        );

        VBox titleBox = new VBox(2);
        pageTitleLabel.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #0f172a;" +
            "-fx-font-family: " + FONT
        );
        pageSubtitleLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #94a3b8;" +
            "-fx-font-family: " + FONT
        );
        titleBox.getChildren().addAll(pageTitleLabel, pageSubtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topbar.getChildren().addAll(titleBox, spacer);
        return topbar;
    }

    // ===================== REMINDER BANNER =====================

    private void buildReminderBanner() {
        reminderBanner.setStyle(
            "-fx-background-color: #fff7ed;" +
            "-fx-border-color: #fdba74;" +
            "-fx-border-width: 0 0 0 4;" +
            "-fx-padding: 12 20;"
        );
        reminderBanner.setSpacing(6);
        reminderBanner.setVisible(false);
        reminderBanner.setManaged(false);

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label bellIcon = new Label("🔔 PENGINGAT DEADLINE");
        bellIcon.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #c2410c;" +
            "-fx-font-family: " + FONT
        );

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        dismissReminderBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #9a3412;" +
            "-fx-font-size: 11px;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: #fdba74;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 2 8;"
        );

        row.getChildren().addAll(bellIcon, sp, dismissReminderBtn);

        reminderLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #9a3412;" +
            "-fx-font-family: " + FONT
        );
        reminderLabel.setWrapText(true);

        reminderBanner.getChildren().addAll(row, reminderLabel);
    }

    // ===================== GROUP SECTION =====================

    @SuppressWarnings("unchecked")
    private void buildGroupSection() {
        groupSection.setSpacing(0);

        // Section header
        HBox secHeader = buildSectionHeader("👥  Tugas Kelompok", "Tugas proyek bersama tim", statusBar, "#3b82f6");

        // Filter bar
        HBox filterBar = buildFilterBar(
            searchField, "🔍  Cari tugas atau proyek...", filterStatus,
            btnAdd, btnEdit, btnChangeStatus, btnDelete, true
        );

        // Table
        taskTable.getColumns().addAll(colId, colTitle, colProject, colAssigned, colPriority, colStatus, colDeadline);
        styleTable(taskTable);
        colId.setPrefWidth(44);
        colTitle.setPrefWidth(220);
        colProject.setPrefWidth(150);
        colAssigned.setPrefWidth(140);
        colPriority.setPrefWidth(90);
        colStatus.setPrefWidth(110);
        colDeadline.setPrefWidth(130);

        setTablePlaceholder(taskTable, "📋", "Belum ada tugas kelompok", "Klik '＋ Tambah' untuk menambahkan tugas baru");
        VBox.setVgrow(taskTable, Priority.ALWAYS);

        groupSection.getChildren().addAll(secHeader, filterBar, taskTable);
    }

    // ===================== PERSONAL SECTION =====================

    @SuppressWarnings("unchecked")
    private void buildPersonalSection() {
        personalSection.setSpacing(0);
        personalSection.setVisible(false);
        personalSection.setManaged(false);

        HBox secHeader = buildSectionHeader("📝  Tugas Mandiri", "Tugas pribadi yang kamu kerjakan sendiri", pStatusBar, "#10b981");

        HBox filterBar = buildFilterBar(
            pSearchField, "🔍  Cari tugas mandiri...", pFilterStatus,
            pBtnAdd, pBtnEdit, pBtnChangeStatus, pBtnDelete, false
        );

        personalTable.getColumns().addAll(pColId, pColTitle, pColCategory, pColPriority, pColStatus, pColDeadline);
        styleTable(personalTable);
        pColId.setPrefWidth(44);
        pColTitle.setPrefWidth(280);
        pColCategory.setPrefWidth(140);
        pColPriority.setPrefWidth(90);
        pColStatus.setPrefWidth(110);
        pColDeadline.setPrefWidth(130);

        setTablePlaceholder(personalTable, "📝", "Belum ada tugas mandiri", "Klik '＋ Tambah' untuk menambahkan tugas mandiri");
        VBox.setVgrow(personalTable, Priority.ALWAYS);

        personalSection.getChildren().addAll(secHeader, filterBar, personalTable);
    }

    // ===================== SHARED BUILDERS =====================

    private HBox buildSectionHeader(String title, String desc, Label statusChip, String accentColor) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 24, 14, 24));
        header.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: transparent transparent #e2e8f0 transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        // Accent bar
        Region bar = new Region();
        bar.setPrefSize(4, 28);
        bar.setStyle("-fx-background-color: " + accentColor + "; -fx-background-radius: 2;");

        Label titleLbl = new Label(title);
        titleLbl.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #0f172a;" +
            "-fx-font-family: " + FONT
        );

        Label descLbl = new Label(desc);
        descLbl.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #94a3b8;" +
            "-fx-font-family: " + FONT
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statusChip.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #64748b;" +
            "-fx-background-color: #f1f5f9;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 3 10;" +
            "-fx-font-family: " + FONT
        );

        header.getChildren().addAll(bar, titleLbl, descLbl, spacer, statusChip);
        return header;
    }

    private HBox buildFilterBar(TextField search, String searchPrompt, ComboBox<String> combo,
                                 Button add, Button edit, Button changeStatus, Button delete,
                                 boolean isPrimary) {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(12, 24, 12, 24));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #f8fafc; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 0 0 1 0;");

        search.setPromptText(searchPrompt);
        search.setPrefWidth(240);
        search.setPrefHeight(36);
        search.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 6 14;" +
            "-fx-font-size: 12.5px;" +
            "-fx-font-family: " + FONT
        );

        combo.setPrefWidth(150);
        combo.setPrefHeight(36);
        combo.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 12.5px;" +
            "-fx-font-family: " + FONT
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String addColor = isPrimary ? "#3b82f6" : "#10b981";
        String addHover = isPrimary ? "#2563eb" : "#059669";
        styleActionBtn(add, addColor, addHover, "white");
        styleActionBtnOutline(edit, "#64748b");
        styleActionBtnOutline(changeStatus, "#64748b");
        styleActionBtnDanger(delete);

        bar.getChildren().addAll(search, combo, spacer, add, edit, changeStatus, delete);
        return bar;
    }

    private void styleActionBtn(Button btn, String bg, String hover, String textColor) {
        String base = "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-font-size: 12.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: " + FONT;
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(base.replace(bg, hover)));
        btn.setOnMouseExited(e -> btn.setStyle(base));
    }

    private void styleActionBtnOutline(Button btn, String color) {
        String base = "-fx-background-color: white;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-font-size: 12.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: " + FONT;
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(base + "-fx-background-color: #f8fafc;"));
        btn.setOnMouseExited(e -> btn.setStyle(base));
    }

    private void styleActionBtnDanger(Button btn) {
        String base = "-fx-background-color: #fff1f2;" +
            "-fx-text-fill: #ef4444;" +
            "-fx-font-size: 12.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #fecaca;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: " + FONT;
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #fee2e2;" +
            "-fx-text-fill: #dc2626;" +
            "-fx-font-size: 12.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #fca5a5;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: " + FONT
        ));
        btn.setOnMouseExited(e -> btn.setStyle(base));
    }

    private <T> void styleTable(TableView<T> table) {
        table.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: transparent;" +
            "-fx-table-cell-border-color: #f1f5f9;" +
            "-fx-font-family: " + FONT +
            "-fx-font-size: 12.5px;"
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private <T> void setTablePlaceholder(TableView<T> table, String emoji, String title, String subtitle) {
        VBox ph = new VBox(8);
        ph.setAlignment(Pos.CENTER);
        ph.setPadding(new Insets(40));

        Label e = new Label(emoji);
        e.setStyle("-fx-font-size: 40px;");

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-family: " + FONT);

        Label s = new Label(subtitle);
        s.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8; -fx-font-family: " + FONT);

        ph.getChildren().addAll(e, t, s);
        table.setPlaceholder(ph);
    }

    private Label buildSectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle(
            "-fx-font-size: 9.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #475569;" +
            "-fx-padding: 12 20 4 20;" +
            "-fx-letter-spacing: 1;" +
            "-fx-font-family: " + FONT
        );
        return lbl;
    }

    private Separator styledSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1e293b;");
        VBox.setMargin(sep, new Insets(4, 0, 4, 0));
        return sep;
    }
}
