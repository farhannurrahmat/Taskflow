package com.taskflow.controller;

import com.taskflow.dao.PersonalTaskDAO;
import com.taskflow.dao.UserDAO;
import com.taskflow.model.PersonalTask;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.service.AuthService;
import com.taskflow.service.TaskService;
import com.taskflow.util.SceneManager;
import com.taskflow.view.DashboardView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    private final DashboardView view;
    private final TaskService taskService = new TaskService();
    private final PersonalTaskDAO personalTaskDAO = new PersonalTaskDAO();
    private final UserDAO userDAO = new UserDAO();
    private ObservableList<Task> allTasks;
    private ObservableList<PersonalTask> allPersonalTasks;

    public DashboardController(DashboardView view) {
        this.view = view;
    }

    public void initialize() {
        User user = AuthService.getCurrentUser();
        view.welcomeLabel.setText(user != null ? user.getFullName() : "");

        // Wire nav buttons
        view.btnNavGroup.setOnAction(e -> showGroupSection());
        view.btnNavPersonal.setOnAction(e -> showPersonalSection());
        view.btnStatistics.setOnAction(e -> SceneManager.switchTo("statistics"));
        view.btnLogout.setOnAction(e -> { AuthService.logout(); SceneManager.switchTo("login"); });

        // Dismiss reminder
        view.dismissReminderBtn.setOnAction(e -> {
            view.reminderBanner.setVisible(false);
            view.reminderBanner.setManaged(false);
        });

        // Toolbar buttons group
        view.btnAdd.setOnAction(e -> showGroupTaskDialog(null));
        view.btnEdit.setOnAction(e -> {
            Task sel = view.taskTable.getSelectionModel().getSelectedItem();
            if (sel != null) showGroupTaskDialog(sel);
        });
        view.btnDelete.setOnAction(e -> handleGroupDelete());
        view.btnChangeStatus.setOnAction(e -> handleGroupChangeStatus());

        // Toolbar buttons personal
        view.pBtnAdd.setOnAction(e -> showPersonalTaskDialog(null));
        view.pBtnEdit.setOnAction(e -> {
            PersonalTask sel = view.personalTable.getSelectionModel().getSelectedItem();
            if (sel != null) showPersonalTaskDialog(sel);
        });
        view.pBtnDelete.setOnAction(e -> handlePersonalDelete());
        view.pBtnChangeStatus.setOnAction(e -> handlePersonalChangeStatus());

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

    private void showGroupSection() {
        view.groupSection.setVisible(true);
        view.groupSection.setManaged(true);
        view.personalSection.setVisible(false);
        view.personalSection.setManaged(false);
        view.styleSidebarBtnActive(view.btnNavGroup);
        view.styleSidebarBtn(view.btnNavPersonal);
        view.pageTitleLabel.setText("Dashboard");
        view.pageSubtitleLabel.setText("Kelola semua tugas kamu di sini");
    }

    private void showPersonalSection() {
        view.groupSection.setVisible(false);
        view.groupSection.setManaged(false);
        view.personalSection.setVisible(true);
        view.personalSection.setManaged(true);
        view.styleSidebarBtn(view.btnNavGroup);
        view.styleSidebarBtnActive(view.btnNavPersonal);
        view.pageTitleLabel.setText("Tugas Mandiri");
        view.pageSubtitleLabel.setText("Tugas pribadi yang kamu kerjakan sendiri");
        loadPersonalTasks();
    }

    // ==================== DEADLINE REMINDERS ====================

    private void showDeadlineReminders() {
        User user = AuthService.getCurrentUser();
        if (user == null) return;

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

        int overdueCount = 0;
        if (allTasks != null) overdueCount += (int) allTasks.stream().filter(Task::isOverdue).count();
        if (allPersonalTasks != null) overdueCount += (int) allPersonalTasks.stream().filter(PersonalTask::isOverdue).count();
        if (overdueCount > 0) reminders.insert(0, "🚨 " + overdueCount + " tugas TERLAMBAT! Segera selesaikan.\n");

        if (reminders.length() > 0) {
            view.reminderLabel.setText(reminders.toString().trim());
            view.reminderBanner.setVisible(true);
            view.reminderBanner.setManaged(true);
        } else {
            view.reminderBanner.setVisible(false);
            view.reminderBanner.setManaged(false);
        }
    }

    // ==================== GROUP TASKS ====================

    private void setupGroupTable() {
        view.colId.setCellValueFactory(cd -> new SimpleStringProperty(""));
        view.colId.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        view.colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        view.colProject.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        view.colAssigned.setCellValueFactory(new PropertyValueFactory<>("assignedToName"));
        view.colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        view.colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        view.colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        view.colPriority.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "High" -> "-fx-text-fill: #ef4444; -fx-font-weight: bold;";
                    case "Medium" -> "-fx-text-fill: #f59e0b; -fx-font-weight: bold;";
                    case "Low" -> "-fx-text-fill: #10b981; -fx-font-weight: bold;";
                    default -> "";
                });
            }
        });

        view.colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "Done" -> "-fx-text-fill: #10b981; -fx-font-weight: bold;";
                    case "In Progress" -> "-fx-text-fill: #3b82f6; -fx-font-weight: bold;";
                    case "To Do" -> "-fx-text-fill: #94a3b8; -fx-font-weight: bold;";
                    default -> "";
                });
            }
        });

        view.colDeadline.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                Task task = getTableRow().getItem();
                setText(item.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                if (task != null && task.isOverdue()) setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                else if (task != null && isTaskDueSoon(task)) setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                else setStyle("");
            }
        });

        view.taskTable.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                setStyle(task == null || empty ? "" :
                    task.isOverdue() ? "-fx-background-color: #fff1f2;" :
                    "Done".equals(task.getStatus()) ? "-fx-background-color: #f0fdf4;" :
                    isTaskDueSoon(task) ? "-fx-background-color: #fffbeb;" : "");
            }
        });

        view.taskTable.getSelectionModel().selectedItemProperty().addListener((obs, old, nv) -> {
            boolean s = nv != null;
            view.btnEdit.setDisable(!s);
            view.btnDelete.setDisable(!s);
            view.btnChangeStatus.setDisable(!s);
        });
        view.btnEdit.setDisable(true);
        view.btnDelete.setDisable(true);
        view.btnChangeStatus.setDisable(true);
    }

    private void setupGroupFilters() {
        view.filterStatus.setItems(FXCollections.observableArrayList("Semua", "To Do", "In Progress", "Done", "Overdue"));
        view.filterStatus.setValue("Semua");
        view.filterStatus.setOnAction(e -> applyGroupFilter());
        view.searchField.textProperty().addListener((obs, o, nw) -> applyGroupFilter());
    }

    private void loadGroupTasks() {
        User user = AuthService.getCurrentUser();
        if (user == null) return;
        ObservableList<Task> tasks = taskService.getTasksForCurrentUser();
        allTasks = tasks;
        view.taskTable.setItems(allTasks);
        updateGroupStatusBar();
    }

    private void applyGroupFilter() {
        String search = view.searchField.getText().toLowerCase();
        String statusFilter = view.filterStatus.getValue();
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
        view.taskTable.setItems(filtered);
    }

    private void updateGroupStatusBar() {
        if (allTasks != null) {
            long overdue = allTasks.stream().filter(Task::isOverdue).count();
            view.statusBar.setText("Total: " + allTasks.size() + " tugas  |  Terlambat: " + overdue);
        }
    }

    private void handleGroupDelete() {
        Task sel = view.taskTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus tugas: " + sel.getTitle() + "?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Hapus Tugas"); confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (taskService.deleteTask(sel.getId())) { loadGroupTasks(); showDeadlineReminders(); }
                else showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus tugas.");
            }
        });
    }

    private void handleGroupChangeStatus() {
        Task sel = view.taskTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        ChoiceDialog<String> dialog = new ChoiceDialog<>(sel.getStatus(), "To Do", "In Progress", "Done");
        dialog.setTitle("Ubah Status"); dialog.setHeaderText("Tugas: " + sel.getTitle());
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
                    super.updateItem(date, empty); setDisable(date.isBefore(LocalDate.now()));
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

        Label validLbl = new Label(); validLbl.setStyle("-fx-text-fill: #ef4444;");
        grid.add(validLbl, 1, row);
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(titleField::requestFocus);

        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (titleField.getText().isBlank()) { validLbl.setText("Judul tidak boleh kosong!"); event.consume(); return; }
            if (projectField.getText().isBlank()) { validLbl.setText("Nama proyek tidak boleh kosong!"); event.consume(); return; }
            if (deadlinePicker.getValue() == null) { validLbl.setText("Deadline harus diisi!"); event.consume(); return; }
            if (existingTask == null && deadlinePicker.getValue().isBefore(LocalDate.now())) {
                validLbl.setText("Deadline tidak boleh sebelum hari ini!"); event.consume();
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
                if ("Done".equals(statusBox.getValue()) && task.getCompletedAt() == null) task.setCompletedAt(LocalDateTime.now());
                else if (!"Done".equals(statusBox.getValue())) task.setCompletedAt(null);
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
        view.pColId.setCellValueFactory(cd -> new SimpleStringProperty(""));
        view.pColId.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        view.pColTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        view.pColCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        view.pColPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        view.pColStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        view.pColDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        view.pColPriority.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "High" -> "-fx-text-fill: #ef4444; -fx-font-weight: bold;";
                    case "Medium" -> "-fx-text-fill: #f59e0b; -fx-font-weight: bold;";
                    case "Low" -> "-fx-text-fill: #10b981; -fx-font-weight: bold;";
                    default -> "";
                });
            }
        });

        view.pColStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "Done" -> "-fx-text-fill: #10b981; -fx-font-weight: bold;";
                    case "In Progress" -> "-fx-text-fill: #3b82f6; -fx-font-weight: bold;";
                    case "To Do" -> "-fx-text-fill: #94a3b8; -fx-font-weight: bold;";
                    default -> "";
                });
            }
        });

        view.pColDeadline.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                PersonalTask task = getTableRow().getItem();
                setText(item.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                if (task != null && task.isOverdue()) setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                else if (task != null && task.isDueSoon()) setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                else setStyle("");
            }
        });

        view.personalTable.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(PersonalTask task, boolean empty) {
                super.updateItem(task, empty);
                setStyle(task == null || empty ? "" :
                    task.isOverdue() ? "-fx-background-color: #fff1f2;" :
                    "Done".equals(task.getStatus()) ? "-fx-background-color: #f0fdf4;" :
                    task.isDueSoon() ? "-fx-background-color: #fffbeb;" : "");
            }
        });

        view.personalTable.getSelectionModel().selectedItemProperty().addListener((obs, old, nv) -> {
            boolean s = nv != null;
            view.pBtnEdit.setDisable(!s);
            view.pBtnDelete.setDisable(!s);
            view.pBtnChangeStatus.setDisable(!s);
        });
        view.pBtnEdit.setDisable(true);
        view.pBtnDelete.setDisable(true);
        view.pBtnChangeStatus.setDisable(true);
    }

    private void setupPersonalFilters() {
        view.pFilterStatus.setItems(FXCollections.observableArrayList("Semua", "To Do", "In Progress", "Done", "Overdue"));
        view.pFilterStatus.setValue("Semua");
        view.pFilterStatus.setOnAction(e -> applyPersonalFilter());
        view.pSearchField.textProperty().addListener((obs, o, nw) -> applyPersonalFilter());
    }

    private void loadPersonalTasks() {
        User user = AuthService.getCurrentUser();
        if (user == null) return;
        allPersonalTasks = user.isManager() ? personalTaskDAO.getAllTasks() : personalTaskDAO.getTasksByUser(user.getId());
        view.personalTable.setItems(allPersonalTasks);
        updatePersonalStatusBar();
    }

    private void applyPersonalFilter() {
        String search = view.pSearchField.getText().toLowerCase();
        String statusFilter = view.pFilterStatus.getValue();
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
        view.personalTable.setItems(filtered);
    }

    private void updatePersonalStatusBar() {
        if (allPersonalTasks != null) {
            long overdue = allPersonalTasks.stream().filter(PersonalTask::isOverdue).count();
            view.pStatusBar.setText("Total: " + allPersonalTasks.size() + " tugas mandiri  |  Terlambat: " + overdue);
        }
    }

    private void handlePersonalDelete() {
        PersonalTask sel = view.personalTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus tugas mandiri: " + sel.getTitle() + "?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Hapus Tugas Mandiri"); confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (personalTaskDAO.deleteTask(sel.getId())) { loadPersonalTasks(); showDeadlineReminders(); }
                else showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus tugas.");
            }
        });
    }

    private void handlePersonalChangeStatus() {
        PersonalTask sel = view.personalTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        ChoiceDialog<String> dialog = new ChoiceDialog<>(sel.getStatus(), "To Do", "In Progress", "Done");
        dialog.setTitle("Ubah Status Tugas Mandiri"); dialog.setHeaderText("Tugas: " + sel.getTitle());
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
                    super.updateItem(date, empty); setDisable(date.isBefore(LocalDate.now()));
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

        Label validLbl = new Label(); validLbl.setStyle("-fx-text-fill: #ef4444;");
        grid.add(validLbl, 1, row);
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(titleField::requestFocus);

        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (titleField.getText().isBlank()) { validLbl.setText("Judul tidak boleh kosong!"); event.consume(); return; }
            if (deadlinePicker.getValue() == null) { validLbl.setText("Deadline harus diisi!"); event.consume(); }
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
                if ("Done".equals(statusBox.getValue()) && task.getCompletedAt() == null) task.setCompletedAt(LocalDateTime.now());
                else if (!"Done".equals(statusBox.getValue())) task.setCompletedAt(null);
                if (user != null) { task.setUserId(user.getId()); task.setUserName(user.getFullName()); }
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isTaskDueSoon(Task task) {
        if (task == null || task.getDeadline() == null) return false;
        return !task.getDeadline().isBefore(LocalDate.now())
            && task.getDeadline().isBefore(LocalDate.now().plusDays(3))
            && !"Done".equals(task.getStatus());
    }
}
