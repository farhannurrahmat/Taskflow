package com.taskflow.controller;

import com.taskflow.dao.PersonalTaskDAO;
import com.taskflow.dao.UserDAO;
import com.taskflow.model.PersonalTask;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.service.AuthService;
import com.taskflow.service.TaskService;
import com.taskflow.util.SceneManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class DashboardController {

    // Sidebar / nav
    @FXML private Label welcomeLabel;
    @FXML private Button btnNavGroup;
    @FXML private Button btnNavPersonal;
    @FXML private Button btnStatistics;
    @FXML private Button btnLogout;

    // Deadline reminder banner
    @FXML private VBox reminderBanner;
    @FXML private Label reminderLabel;

    // Group task table
    @FXML private VBox groupSection;
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> colId;
    @FXML private TableColumn<Task, String> colTitle;
    @FXML private TableColumn<Task, String> colProject;
    @FXML private TableColumn<Task, String> colAssigned;
    @FXML private TableColumn<Task, String> colPriority;
    @FXML private TableColumn<Task, String> colStatus;
    @FXML private TableColumn<Task, LocalDate> colDeadline;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnChangeStatus;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatus;
    @FXML private Label statusBar;

    // Personal task table
    @FXML private VBox personalSection;
    @FXML private TableView<PersonalTask> personalTable;
    @FXML private TableColumn<PersonalTask, String> pColId;
    @FXML private TableColumn<PersonalTask, String> pColTitle;
    @FXML private TableColumn<PersonalTask, String> pColCategory;
    @FXML private TableColumn<PersonalTask, String> pColPriority;
    @FXML private TableColumn<PersonalTask, String> pColStatus;
    @FXML private TableColumn<PersonalTask, LocalDate> pColDeadline;
    @FXML private Button pBtnAdd;
    @FXML private Button pBtnEdit;
    @FXML private Button pBtnDelete;
    @FXML private Button pBtnChangeStatus;
    @FXML private TextField pSearchField;
    @FXML private ComboBox<String> pFilterStatus;
    @FXML private Label pStatusBar;

    private final TaskService taskService = new TaskService();
    private final PersonalTaskDAO personalTaskDAO = new PersonalTaskDAO();
    private final UserDAO userDAO = new UserDAO();
    private ObservableList<Task> allTasks;
    private ObservableList<PersonalTask> allPersonalTasks;

    private boolean showingPersonal = false;

    @FXML
    public void initialize() {
        User user = AuthService.getCurrentUser();
        welcomeLabel.setText(user != null ? user.getFullName() : "");

        setupGroupTable();
        setupPersonalTable();
        setupGroupFilters();
        setupPersonalFilters();
        loadGroupTasks();
        loadPersonalTasks();
        showGroupSection();
        showDeadlineReminders();
    }

    // ==================== NAVIGATION ====================

    @FXML
    private void showGroupSection() {
        showingPersonal = false;
        groupSection.setVisible(true);
        groupSection.setManaged(true);
        personalSection.setVisible(false);
        personalSection.setManaged(false);
        btnNavGroup.getStyleClass().remove("sidebar-btn");
        btnNavGroup.getStyleClass().add("sidebar-btn-active");
        btnNavPersonal.getStyleClass().remove("sidebar-btn-active");
        btnNavPersonal.getStyleClass().add("sidebar-btn");
    }

    @FXML
    private void showPersonalSection() {
        showingPersonal = true;
        groupSection.setVisible(false);
        groupSection.setManaged(false);
        personalSection.setVisible(true);
        personalSection.setManaged(true);
        btnNavPersonal.getStyleClass().remove("sidebar-btn");
        btnNavPersonal.getStyleClass().add("sidebar-btn-active");
        btnNavGroup.getStyleClass().remove("sidebar-btn-active");
        btnNavGroup.getStyleClass().add("sidebar-btn");
        loadPersonalTasks();
    }

    // ==================== DEADLINE REMINDERS ====================

    private void showDeadlineReminders() {
        User user = AuthService.getCurrentUser();
        if (user == null) return;

        // Check group tasks due soon (within 2 days)
        StringBuilder reminders = new StringBuilder();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

        if (allTasks != null) {
            for (Task t : allTasks) {
                if (!t.isOverdue() && t.getDeadline() != null
                        && !t.getDeadline().isBefore(LocalDate.now())
                        && t.getDeadline().isBefore(LocalDate.now().plusDays(3))
                        && !"Done".equals(t.getStatus())) {
                    long daysLeft = LocalDate.now().until(t.getDeadline()).getDays();
                    String label = daysLeft == 0 ? "HARI INI" : daysLeft + " hari lagi";
                    reminders.append("⚠️ [Kelompok] ").append(t.getTitle())
                             .append(" — ").append(label)
                             .append(" (").append(t.getDeadline().format(fmt)).append(")\n");
                }
            }
        }

        if (allPersonalTasks != null) {
            for (PersonalTask pt : allPersonalTasks) {
                if (!pt.isOverdue() && pt.getDeadline() != null
                        && !pt.getDeadline().isBefore(LocalDate.now())
                        && pt.getDeadline().isBefore(LocalDate.now().plusDays(3))
                        && !"Done".equals(pt.getStatus())) {
                    long daysLeft = LocalDate.now().until(pt.getDeadline()).getDays();
                    String label = daysLeft == 0 ? "HARI INI" : daysLeft + " hari lagi";
                    reminders.append("🔔 [Mandiri] ").append(pt.getTitle())
                             .append(" — ").append(label)
                             .append(" (").append(pt.getDeadline().format(fmt)).append(")\n");
                }
            }
        }

        // Also check overdue
        int overdueCount = 0;
        if (allTasks != null) overdueCount += allTasks.stream().filter(Task::isOverdue).count();
        if (allPersonalTasks != null) overdueCount += allPersonalTasks.stream().filter(PersonalTask::isOverdue).count();

        if (overdueCount > 0) {
            reminders.insert(0, "🚨 " + overdueCount + " tugas TERLAMBAT! Segera selesaikan.\n");
        }

        if (reminders.length() > 0) {
            reminderLabel.setText(reminders.toString().trim());
            reminderBanner.setVisible(true);
            reminderBanner.setManaged(true);
        } else {
            reminderBanner.setVisible(false);
            reminderBanner.setManaged(false);
        }
    }

    @FXML
    private void dismissReminder() {
        reminderBanner.setVisible(false);
        reminderBanner.setManaged(false);
    }

    // ==================== GROUP TASKS ====================

    private void setupGroupTable() {
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(""));
        colId.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colProject.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        colAssigned.setCellValueFactory(new PropertyValueFactory<>("assignedToName"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        colPriority.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "High" -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    case "Medium" -> setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                    case "Low" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    default -> setStyle("");
                }
            }
        });

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "Done" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    case "In Progress" -> setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
                    case "To Do" -> setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: bold;");
                    default -> setStyle("");
                }
            }
        });

        colDeadline.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                Task task = getTableRow().getItem();
                setText(item.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                if (task != null && task.isOverdue()) {
                    setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                } else if (task != null && !task.isOverdue()
                           && item.isBefore(LocalDate.now().plusDays(3))
                           && !"Done".equals(task.getStatus())) {
                    setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });

        taskTable.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                getStyleClass().removeAll("row-overdue", "row-done", "row-soon");
                if (task == null || empty) return;
                if (task.isOverdue()) getStyleClass().add("row-overdue");
                else if ("Done".equals(task.getStatus())) getStyleClass().add("row-done");
                else if (task.getDeadline() != null && task.getDeadline().isBefore(LocalDate.now().plusDays(3)))
                    getStyleClass().add("row-soon");
            }
        });

        User user = AuthService.getCurrentUser();
        colAssigned.setVisible(user != null && user.isManager());

        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            boolean sel = newVal != null;
            btnEdit.setDisable(!sel);
            btnDelete.setDisable(!sel);
            btnChangeStatus.setDisable(!sel);
        });
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
        btnChangeStatus.setDisable(true);
    }

    private void setupGroupFilters() {
        filterStatus.setItems(FXCollections.observableArrayList("Semua", "To Do", "In Progress", "Done", "Overdue"));
        filterStatus.setValue("Semua");
        filterStatus.setOnAction(e -> applyGroupFilter());
        searchField.textProperty().addListener((obs, old, nw) -> applyGroupFilter());
    }

    private void loadGroupTasks() {
        allTasks = taskService.getTasksForCurrentUser();
        taskTable.setItems(allTasks);
        updateGroupStatusBar();
    }

    private void applyGroupFilter() {
        String search = searchField.getText().toLowerCase();
        String statusFilter = filterStatus.getValue();
        ObservableList<Task> filtered = FXCollections.observableArrayList();
        for (Task t : allTasks) {
            boolean matchSearch = t.getTitle().toLowerCase().contains(search)
                || t.getProjectName().toLowerCase().contains(search)
                || (t.getAssignedToName() != null && t.getAssignedToName().toLowerCase().contains(search));
            boolean matchStatus = switch (statusFilter) {
                case "Semua" -> true;
                case "Overdue" -> t.isOverdue();
                default -> statusFilter.equals(t.getStatus());
            };
            if (matchSearch && matchStatus) filtered.add(t);
        }
        taskTable.setItems(filtered);
    }

    private void updateGroupStatusBar() {
        if (allTasks != null) {
            long overdue = allTasks.stream().filter(Task::isOverdue).count();
            statusBar.setText("Total: " + allTasks.size() + " tugas | Terlambat: " + overdue);
        }
    }

    @FXML private void handleAdd() { showGroupTaskDialog(null); }
    @FXML private void handleEdit() {
        Task sel = taskTable.getSelectionModel().getSelectedItem();
        if (sel != null) showGroupTaskDialog(sel);
    }

    @FXML private void handleDelete() {
        Task sel = taskTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus tugas: " + sel.getTitle() + "?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Hapus Tugas");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (taskService.deleteTask(sel.getId())) { loadGroupTasks(); showDeadlineReminders(); }
                else showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus tugas.");
            }
        });
    }

    @FXML private void handleChangeStatus() {
        Task sel = taskTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        ChoiceDialog<String> dialog = new ChoiceDialog<>(sel.getStatus(), "To Do", "In Progress", "Done");
        dialog.setTitle("Ubah Status");
        dialog.setHeaderText("Tugas: " + sel.getTitle());
        dialog.setContentText("Pilih status baru:");
        dialog.showAndWait().ifPresent(s -> {
            if (taskService.updateStatus(sel.getId(), s)) { loadGroupTasks(); showDeadlineReminders(); }
        });
    }

    private void showGroupTaskDialog(Task existingTask) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle(existingTask == null ? "Tambah Tugas Kelompok" : "Edit Tugas");
        dialog.setHeaderText(existingTask == null ? "Isi detail tugas baru" : "Edit detail tugas");

        ButtonType saveBtn = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 120, 10, 10));

        TextField titleField = new TextField(existingTask != null ? existingTask.getTitle() : "");
        titleField.setPromptText("Judul tugas");
        TextArea descField = new TextArea(existingTask != null ? existingTask.getDescription() : "");
        descField.setPromptText("Deskripsi"); descField.setPrefRowCount(2);
        TextField projectField = new TextField(existingTask != null ? existingTask.getProjectName() : "");
        projectField.setPromptText("Nama proyek / kelompok");
        ComboBox<String> priorityBox = new ComboBox<>(FXCollections.observableArrayList("High", "Medium", "Low"));
        priorityBox.setValue(existingTask != null ? existingTask.getPriority() : "Medium");
        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList("To Do", "In Progress", "Done"));
        statusBox.setValue(existingTask != null ? existingTask.getStatus() : "To Do");
        DatePicker deadlinePicker = new DatePicker(existingTask != null ? existingTask.getDeadline() : LocalDate.now().plusDays(7));
        if (existingTask == null) {
            deadlinePicker.setDayCellFactory(p -> new DateCell() {
                @Override public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(date.isBefore(LocalDate.now()));
                }
            });
        }

        User currentUser = AuthService.getCurrentUser();
        ComboBox<User> assignedBox = new ComboBox<>();
        List<User> members = userDAO.getAllMembers();
        assignedBox.setItems(FXCollections.observableArrayList(members));
        if (existingTask != null) {
            members.stream().filter(u -> u.getId() == existingTask.getAssignedTo()).findFirst().ifPresent(assignedBox::setValue);
        } else if (!members.isEmpty()) assignedBox.setValue(members.get(0));

        int row = 0;
        grid.add(new Label("Judul*:"), 0, row); grid.add(titleField, 1, row++);
        grid.add(new Label("Deskripsi:"), 0, row); grid.add(descField, 1, row++);
        grid.add(new Label("Proyek/Kelompok*:"), 0, row); grid.add(projectField, 1, row++);
        grid.add(new Label("Prioritas:"), 0, row); grid.add(priorityBox, 1, row++);
        grid.add(new Label("Status:"), 0, row); grid.add(statusBox, 1, row++);
        grid.add(new Label("Deadline*:"), 0, row); grid.add(deadlinePicker, 1, row++);
        if (currentUser != null && currentUser.isManager()) {
            grid.add(new Label("Ditugaskan ke:"), 0, row); grid.add(assignedBox, 1, row++);
        }

        Label validLbl = new Label(); validLbl.setStyle("-fx-text-fill: #e74c3c;");
        grid.add(validLbl, 1, row);
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(titleField::requestFocus);

        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (titleField.getText().isBlank()) { validLbl.setText("Judul tidak boleh kosong!"); event.consume(); return; }
            if (projectField.getText().isBlank()) { validLbl.setText("Nama proyek tidak boleh kosong!"); event.consume(); return; }
            if (deadlinePicker.getValue() == null) { validLbl.setText("Deadline harus diisi!"); event.consume(); return; }
            if (existingTask == null && deadlinePicker.getValue().isBefore(LocalDate.now())) {
                validLbl.setText("Deadline tidak boleh sebelum hari ini!"); event.consume(); return;
            }
        });

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Task task = existingTask != null ? existingTask : new Task();
                task.setTitle(titleField.getText().trim());
                task.setDescription(descField.getText().trim());
                task.setProjectName(projectField.getText().trim());
                task.setPriority(priorityBox.getValue());
                task.setStatus(statusBox.getValue());
                task.setDeadline(deadlinePicker.getValue());
                if ("Done".equals(statusBox.getValue()) && task.getCompletedAt() == null)
                    task.setCompletedAt(LocalDateTime.now());
                else if (!"Done".equals(statusBox.getValue()))
                    task.setCompletedAt(null);
                if (currentUser != null && currentUser.isManager() && assignedBox.getValue() != null) {
                    task.setAssignedTo(assignedBox.getValue().getId());
                    task.setAssignedToName(assignedBox.getValue().getFullName());
                } else if (existingTask == null && currentUser != null) {
                    task.setAssignedTo(currentUser.getId());
                    task.setAssignedToName(currentUser.getFullName());
                }
                return task;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(task -> {
            boolean success = existingTask == null ? taskService.addTask(task) : taskService.updateTask(task);
            if (success) { loadGroupTasks(); showDeadlineReminders(); }
            else showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan tugas.");
        });
    }

    // ==================== PERSONAL TASKS ====================

    private void setupPersonalTable() {
        pColId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(""));
        pColId.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        pColTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        pColCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        pColPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        pColStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        pColDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        pColPriority.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "High" -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    case "Medium" -> setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                    case "Low" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    default -> setStyle("");
                }
            }
        });

        pColStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "Done" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    case "In Progress" -> setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
                    case "To Do" -> setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: bold;");
                    default -> setStyle("");
                }
            }
        });

        pColDeadline.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                PersonalTask task = getTableRow().getItem();
                setText(item.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                if (task != null && task.isOverdue()) setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                else if (task != null && task.isDueSoon()) setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                else setStyle("");
            }
        });

        personalTable.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(PersonalTask task, boolean empty) {
                super.updateItem(task, empty);
                getStyleClass().removeAll("row-overdue", "row-done", "row-soon");
                if (task == null || empty) return;
                if (task.isOverdue()) getStyleClass().add("row-overdue");
                else if ("Done".equals(task.getStatus())) getStyleClass().add("row-done");
                else if (task.isDueSoon()) getStyleClass().add("row-soon");
            }
        });

        personalTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            boolean sel = newVal != null;
            pBtnEdit.setDisable(!sel);
            pBtnDelete.setDisable(!sel);
            pBtnChangeStatus.setDisable(!sel);
        });
        pBtnEdit.setDisable(true);
        pBtnDelete.setDisable(true);
        pBtnChangeStatus.setDisable(true);
    }

    private void setupPersonalFilters() {
        pFilterStatus.setItems(FXCollections.observableArrayList("Semua", "To Do", "In Progress", "Done", "Overdue"));
        pFilterStatus.setValue("Semua");
        pFilterStatus.setOnAction(e -> applyPersonalFilter());
        pSearchField.textProperty().addListener((obs, old, nw) -> applyPersonalFilter());
    }

    private void loadPersonalTasks() {
        User user = AuthService.getCurrentUser();
        if (user == null) return;
        if (user.isManager()) {
            allPersonalTasks = personalTaskDAO.getAllTasks();
        } else {
            allPersonalTasks = personalTaskDAO.getTasksByUser(user.getId());
        }
        personalTable.setItems(allPersonalTasks);
        updatePersonalStatusBar();
    }

    private void applyPersonalFilter() {
        String search = pSearchField.getText().toLowerCase();
        String statusFilter = pFilterStatus.getValue();
        ObservableList<PersonalTask> filtered = FXCollections.observableArrayList();
        for (PersonalTask t : allPersonalTasks) {
            boolean matchSearch = t.getTitle().toLowerCase().contains(search)
                || t.getCategory().toLowerCase().contains(search);
            boolean matchStatus = switch (statusFilter) {
                case "Semua" -> true;
                case "Overdue" -> t.isOverdue();
                default -> statusFilter.equals(t.getStatus());
            };
            if (matchSearch && matchStatus) filtered.add(t);
        }
        personalTable.setItems(filtered);
    }

    private void updatePersonalStatusBar() {
        if (allPersonalTasks != null) {
            long overdue = allPersonalTasks.stream().filter(PersonalTask::isOverdue).count();
            pStatusBar.setText("Total: " + allPersonalTasks.size() + " tugas mandiri | Terlambat: " + overdue);
        }
    }

    @FXML private void handlePersonalAdd() { showPersonalTaskDialog(null); }
    @FXML private void handlePersonalEdit() {
        PersonalTask sel = personalTable.getSelectionModel().getSelectedItem();
        if (sel != null) showPersonalTaskDialog(sel);
    }
    @FXML private void handlePersonalDelete() {
        PersonalTask sel = personalTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus tugas mandiri: " + sel.getTitle() + "?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Hapus Tugas Mandiri");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (personalTaskDAO.deleteTask(sel.getId())) { loadPersonalTasks(); showDeadlineReminders(); }
                else showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus tugas.");
            }
        });
    }
    @FXML private void handlePersonalChangeStatus() {
        PersonalTask sel = personalTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        ChoiceDialog<String> dialog = new ChoiceDialog<>(sel.getStatus(), "To Do", "In Progress", "Done");
        dialog.setTitle("Ubah Status Tugas Mandiri");
        dialog.setHeaderText("Tugas: " + sel.getTitle());
        dialog.setContentText("Pilih status baru:");
        dialog.showAndWait().ifPresent(s -> {
            if (personalTaskDAO.updateStatus(sel.getId(), s)) { loadPersonalTasks(); showDeadlineReminders(); }
        });
    }

    private void showPersonalTaskDialog(PersonalTask existing) {
        Dialog<PersonalTask> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Tambah Tugas Mandiri" : "Edit Tugas Mandiri");
        dialog.setHeaderText(existing == null ? "Tambahkan tugas mandiri Anda" : "Edit tugas mandiri");

        ButtonType saveBtn = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 120, 10, 10));

        TextField titleField = new TextField(existing != null ? existing.getTitle() : "");
        titleField.setPromptText("Judul tugas mandiri");
        TextArea descField = new TextArea(existing != null ? existing.getDescription() : "");
        descField.setPromptText("Deskripsi / catatan"); descField.setPrefRowCount(2);
        ComboBox<String> categoryBox = new ComboBox<>(FXCollections.observableArrayList(
            "Kuliah", "Tugas Kuliah", "Penelitian", "Praktikum", "Skripsi", "Pribadi", "Lainnya"
        ));
        categoryBox.setValue(existing != null ? existing.getCategory() : "Kuliah");
        categoryBox.setEditable(true);
        ComboBox<String> priorityBox = new ComboBox<>(FXCollections.observableArrayList("High", "Medium", "Low"));
        priorityBox.setValue(existing != null ? existing.getPriority() : "Medium");
        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList("To Do", "In Progress", "Done"));
        statusBox.setValue(existing != null ? existing.getStatus() : "To Do");
        DatePicker deadlinePicker = new DatePicker(existing != null ? existing.getDeadline() : LocalDate.now().plusDays(7));
        if (existing == null) {
            deadlinePicker.setDayCellFactory(p -> new DateCell() {
                @Override public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(date.isBefore(LocalDate.now()));
                }
            });
        }

        int row = 0;
        grid.add(new Label("Judul*:"), 0, row); grid.add(titleField, 1, row++);
        grid.add(new Label("Deskripsi:"), 0, row); grid.add(descField, 1, row++);
        grid.add(new Label("Kategori:"), 0, row); grid.add(categoryBox, 1, row++);
        grid.add(new Label("Prioritas:"), 0, row); grid.add(priorityBox, 1, row++);
        grid.add(new Label("Status:"), 0, row); grid.add(statusBox, 1, row++);
        grid.add(new Label("Deadline*:"), 0, row); grid.add(deadlinePicker, 1, row++);

        Label validLbl = new Label(); validLbl.setStyle("-fx-text-fill: #e74c3c;");
        grid.add(validLbl, 1, row);
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(titleField::requestFocus);

        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (titleField.getText().isBlank()) { validLbl.setText("Judul tidak boleh kosong!"); event.consume(); return; }
            if (deadlinePicker.getValue() == null) { validLbl.setText("Deadline harus diisi!"); event.consume(); return; }
        });

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                User user = AuthService.getCurrentUser();
                PersonalTask task = existing != null ? existing : new PersonalTask();
                task.setTitle(titleField.getText().trim());
                task.setDescription(descField.getText().trim());
                task.setCategory(categoryBox.getValue());
                task.setProjectName(categoryBox.getValue());
                task.setPriority(priorityBox.getValue());
                task.setStatus(statusBox.getValue());
                task.setDeadline(deadlinePicker.getValue());
                if ("Done".equals(statusBox.getValue()) && task.getCompletedAt() == null)
                    task.setCompletedAt(LocalDateTime.now());
                else if (!"Done".equals(statusBox.getValue()))
                    task.setCompletedAt(null);
                if (user != null) {
                    task.setUserId(user.getId());
                    task.setUserName(user.getFullName());
                }
                return task;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(task -> {
            boolean success = existing == null ? personalTaskDAO.addTask(task) : personalTaskDAO.updateTask(task);
            if (success) { loadPersonalTasks(); showDeadlineReminders(); }
            else showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan tugas mandiri.");
        });
    }

    // ==================== SHARED ACTIONS ====================

    @FXML private void handleStatistics() { SceneManager.switchTo("statistics"); }

    @FXML private void handleLogout() {
        AuthService.logout();
        SceneManager.switchTo("login");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
