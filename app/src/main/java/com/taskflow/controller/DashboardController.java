package com.taskflow.controller;

import com.taskflow.model.Task;
import com.taskflow.model.Task.Priority;
import com.taskflow.model.Task.Status;
import com.taskflow.service.AuthService;
import com.taskflow.service.TaskService;
import com.taskflow.util.SceneManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private VBox taskListContainer;
    @FXML private Label totalLabel;
    @FXML private Label selesaiLabel;
    @FXML private Label belumLabel;

    private TaskService taskService = TaskService.getInstance();
    private AuthService authService = AuthService.getInstance();

    @FXML
    public void initialize() {
        String name = authService.getCurrentUser().getFullName();
        welcomeLabel.setText("Selamat datang, " + name + "!");
        refreshTaskList();
    }

    private void refreshTaskList() {
        taskListContainer.getChildren().clear();
        List<Task> tasks = taskService.getAllTasks();

        for (Task task : tasks) {
            taskListContainer.getChildren().add(buildTaskCard(task));
        }

        totalLabel.setText(String.valueOf(taskService.getTotalTasks()));
        selesaiLabel.setText(String.valueOf(taskService.getCompletedTasks()));
        belumLabel.setText(String.valueOf(taskService.getPendingTasks()));
    }

    private VBox buildTaskCard(Task task) {
        VBox card = new VBox(6);
        card.getStyleClass().add("task-card");
        card.setPadding(new Insets(12));

        // Priority color bar
        String prioColor = task.getPriority() == Priority.TINGGI ? "#ef4444"
                : task.getPriority() == Priority.SEDANG ? "#f59e0b" : "#22c55e";

        Label titleLabel = new Label(task.getTitle());
        titleLabel.getStyleClass().add("task-title");

        Label descLabel = new Label(task.getDescription());
        descLabel.getStyleClass().add("task-desc");
        descLabel.setWrapText(true);

        Label metaLabel = new Label("📚 " + task.getMataKuliah()
                + "   📅 " + task.getDeadline()
                + "   🏷 " + task.getPriority().getLabel());
        metaLabel.getStyleClass().add("task-meta");

        Label statusBadge = new Label(task.getStatusLabel());
        statusBadge.getStyleClass().addAll("status-badge", "status-" + task.getStatus().name().toLowerCase());

        if (task.isOverdue()) {
            card.getStyleClass().add("task-overdue");
        }

        // Action buttons row
        HBox actions = new HBox(8);

        // Status combo
        ComboBox<Status> statusCombo = new ComboBox<>(
                FXCollections.observableArrayList(Status.values()));
        statusCombo.setValue(task.getStatus());
        statusCombo.getStyleClass().add("status-combo");
        statusCombo.setOnAction(e -> {
            taskService.updateStatus(task.getId(), statusCombo.getValue());
            refreshTaskList();
        });

        Button editBtn = new Button("✏ Edit");
        editBtn.getStyleClass().add("btn-edit");
        editBtn.setOnAction(e -> showEditDialog(task));

        Button deleteBtn = new Button("🗑 Hapus");
        deleteBtn.getStyleClass().add("btn-delete");
        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Hapus tugas \"" + task.getTitle() + "\"?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    taskService.deleteTask(task.getId());
                    refreshTaskList();
                }
            });
        });

        actions.getChildren().addAll(statusCombo, editBtn, deleteBtn);

        HBox top = new HBox(10);
        top.getChildren().addAll(titleLabel, statusBadge);

        card.getChildren().addAll(top, descLabel, metaLabel, actions);
        return card;
    }

    @FXML
    private void handleAddTask() {
        showAddDialog();
    }

    private void showAddDialog() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Tambah Tugas Baru");
        dialog.setHeaderText("Isi informasi tugas baru");

        ButtonType saveType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = buildTaskForm(null);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/com/taskflow/css/style.css").toExternalForm());

        TextField titleF = (TextField) grid.lookup("#titleField");
        TextArea descF = (TextArea) grid.lookup("#descField");
        DatePicker deadlineF = (DatePicker) grid.lookup("#deadlinePicker");
        ComboBox<Priority> prioF = (ComboBox<Priority>) grid.lookup("#prioCombo");
        TextField mkF = (TextField) grid.lookup("#mkField");

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                Task t = new Task(0, titleF.getText(), descF.getText(),
                        deadlineF.getValue() != null ? deadlineF.getValue() : LocalDate.now().plusDays(7),
                        prioF.getValue() != null ? prioF.getValue() : Priority.SEDANG,
                        mkF.getText());
                return t;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(t -> {
            taskService.addTask(t);
            refreshTaskList();
        });
    }

    private void showEditDialog(Task task) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Edit Tugas");
        dialog.setHeaderText("Ubah informasi tugas");

        ButtonType saveType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = buildTaskForm(task);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/com/taskflow/css/style.css").toExternalForm());

        TextField titleF = (TextField) grid.lookup("#titleField");
        TextArea descF = (TextArea) grid.lookup("#descField");
        DatePicker deadlineF = (DatePicker) grid.lookup("#deadlinePicker");
        ComboBox<Priority> prioF = (ComboBox<Priority>) grid.lookup("#prioCombo");
        TextField mkF = (TextField) grid.lookup("#mkField");

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                task.setTitle(titleF.getText());
                task.setDescription(descF.getText());
                if (deadlineF.getValue() != null) task.setDeadline(deadlineF.getValue());
                if (prioF.getValue() != null) task.setPriority(prioF.getValue());
                task.setMataKuliah(mkF.getText());
                return task;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(t -> {
            taskService.updateTask(t);
            refreshTaskList();
        });
    }

    private GridPane buildTaskForm(Task task) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField(task != null ? task.getTitle() : "");
        titleField.setId("titleField");
        titleField.setPromptText("Judul tugas");
        titleField.setPrefWidth(320);

        TextArea descField = new TextArea(task != null ? task.getDescription() : "");
        descField.setId("descField");
        descField.setPromptText("Deskripsi tugas");
        descField.setPrefRowCount(3);

        DatePicker deadlinePicker = new DatePicker(task != null ? task.getDeadline() : LocalDate.now().plusDays(7));
        deadlinePicker.setId("deadlinePicker");

        ComboBox<Priority> prioCombo = new ComboBox<>(
                FXCollections.observableArrayList(Priority.values()));
        prioCombo.setId("prioCombo");
        prioCombo.setValue(task != null ? task.getPriority() : Priority.SEDANG);
        prioCombo.setConverter(new javafx.util.StringConverter<Priority>() {
            public String toString(Priority p) { return p != null ? p.getLabel() : ""; }
            public Priority fromString(String s) { return null; }
        });

        TextField mkField = new TextField(task != null ? task.getMataKuliah() : "");
        mkField.setId("mkField");
        mkField.setPromptText("Nama mata kuliah");

        grid.add(new Label("Judul:"), 0, 0); grid.add(titleField, 1, 0);
        grid.add(new Label("Deskripsi:"), 0, 1); grid.add(descField, 1, 1);
        grid.add(new Label("Deadline:"), 0, 2); grid.add(deadlinePicker, 1, 2);
        grid.add(new Label("Prioritas:"), 0, 3); grid.add(prioCombo, 1, 3);
        grid.add(new Label("Mata Kuliah:"), 0, 4); grid.add(mkField, 1, 4);

        return grid;
    }

    @FXML
    private void handleStatistics() {
        SceneManager.switchToStatistics();
    }

    @FXML
    private void handleLogout() {
        authService.logout();
        SceneManager.switchToLogin();
    }
}
