package com.taskflow.controller;

import com.taskflow.dao.PersonalTaskDAO;
import com.taskflow.model.PersonalTask;
import com.taskflow.model.User;
import com.taskflow.service.AuthService;
import com.taskflow.util.SceneManager;
import com.taskflow.view.DashboardView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    private final DashboardView view;
    private final PersonalTaskDAO personalTaskDAO = new PersonalTaskDAO();

    public DashboardController(DashboardView view) {
        this.view = view;
    }

    public void initialize() {
        User user = AuthService.getCurrentUser();
        view.welcomeLabel.setText(user != null ? user.getFullName() : "Pengguna");

        // Navigasi Sidebar
        view.btnDashboard.setOnAction(e -> loadTasks());
        view.btnStatistics.setOnAction(e -> SceneManager.switchTo("statistics"));
        view.btnLogout.setOnAction(e -> {
            AuthService.logout();
            SceneManager.switchTo("login");
        });
        view.btnQuickAdd.setOnAction(e -> showTaskDialog(null));
        loadTasks();
    }

    private void loadTasks() {
        view.urgentTasksContainer.getChildren().clear();
        view.upcomingTasksContainer.getChildren().clear();
        view.completedTasksContainer.getChildren().clear();
        
        User user = AuthService.getCurrentUser();
        if (user == null) return;

        List<PersonalTask> tasks = personalTaskDAO.getTasksByUser(user.getId());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

        for (PersonalTask task : tasks) {
            String deadlineStr = task.getDeadline() != null ? task.getDeadline().format(fmt) : "-";
            
            // Logika cerdas membaca kategori dan tipe dari database
            String cat = task.getCategory();
            if (cat == null || cat.trim().isEmpty()) {
                cat = "Akademik • Personal";
            } else if (!cat.contains(" • ")) {
                cat = cat + " • Personal"; // Fallback untuk data lama yang belum punya tipe
            }
            
            String status = task.getStatus() == null ? "To Do" : task.getStatus();
            String priority = task.getPriority() == null ? "Medium" : task.getPriority();
            VBox card = view.createTaskCard(task.getTitle(), cat, deadlineStr, priority, status);

            card.prefWidthProperty().bind(
                    view.urgentTasksContainer.widthProperty()
                            .subtract(45)
                            .divide(4)
                            .subtract(2));
            card.setOnMouseClicked(e -> showTaskDialog(task));

            if ("Done".equals(status)) {
                view.completedTasksContainer.getChildren().add(card);
            } else if (task.getDeadline() != null && !task.getDeadline().isAfter(LocalDate.now().plusDays(1))) {
                view.urgentTasksContainer.getChildren().add(card);
            } else {
                view.upcomingTasksContainer.getChildren().add(card);
            }
        }

        int totalTasks = tasks.size();
        int doneTasks = (int) tasks.stream().filter(t -> "Done".equals(t.getStatus())).count();

        if (totalTasks > 0) {
            double progress = (double) doneTasks / totalTasks;
            view.dailyProgressBar.setProgress(progress);
            view.progressLabel.setText(Math.round(progress * 100) + "% Diselesaikan");

            if (progress >= 1.0) {
                view.progressLabel.setText("🎉 Semua Tugas Selesai!");
                view.dailyProgressBar.setStyle("-fx-accent: #3b82f6; -fx-control-inner-background: #f1f5f9;");
            } else {
                view.dailyProgressBar.setStyle("-fx-accent: #10b981; -fx-control-inner-background: #f1f5f9;");
            }
        } else {
            view.dailyProgressBar.setProgress(0);
            view.progressLabel.setText("Belum ada tugas");
        }
    }

    private void showTaskDialog(PersonalTask existing) {
        Dialog<PersonalTask> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Tambah Tugas Baru" : "Detail & Edit Tugas");
        dialog.setHeaderText(existing == null ? "Fokuskan targetmu hari ini" : "Edit deskripsi atau ubah status tugas");

        ButtonType saveBtn = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField titleField = new TextField(existing != null ? existing.getTitle() : "");
        titleField.setPromptText("Judul tugas");

        TextArea descField = new TextArea(
                existing != null && existing.getDescription() != null ? existing.getDescription() : "");
        descField.setPromptText("Tuliskan detail, catatan, atau link referensi tugas di sini...");
        descField.setPrefRowCount(3);
        descField.setWrapText(true);

        // --- LOGIKA MEMECAH DATA SAAT EDIT TUGAS ---
        String defaultCat = "Akademik";
        String defaultType = "Personal";
        
        if (existing != null && existing.getCategory() != null) {
            String[] parts = existing.getCategory().split(" • ");
            defaultCat = parts[0];
            if (parts.length > 1) {
                defaultType = parts[1];
            }
        }

        ComboBox<String> categoryBox = new ComboBox<>(FXCollections.observableArrayList(
                "Akademik",
                "Organisasi",
                "Pengembangan Diri",
                "Bisnis & Keuangan",
                "Kesehatan",
                "Pribadi"));
        categoryBox.setValue(defaultCat);
        categoryBox.setEditable(true);

        // --- DROPDOWN BARU UNTUK TIPE TUGAS ---
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("Personal", "Tim"));
        typeBox.setValue(defaultType);

        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList("To Do", "In Progress", "Done"));
        statusBox.setValue(existing != null ? existing.getStatus() : "To Do");

        DatePicker deadlinePicker = new DatePicker(
                existing != null ? existing.getDeadline() : LocalDate.now().plusDays(1));

        int row = 0;
        grid.add(new Label("Judul*:"), 0, row);
        grid.add(titleField, 1, row++);
        grid.add(new Label("Deskripsi:"), 0, row);
        grid.add(descField, 1, row++); 
        grid.add(new Label("Deadline*:"), 0, row);
        grid.add(deadlinePicker, 1, row++);
        grid.add(new Label("Kategori:"), 0, row);
        grid.add(categoryBox, 1, row++);
        
        // --- MEMASUKKAN INPUTAN TIPE KE DALAM FORM ---
        grid.add(new Label("Tipe Tugas:"), 0, row);
        grid.add(typeBox, 1, row++);
        
        grid.add(new Label("Status:"), 0, row);
        grid.add(statusBox, 1, row++);

        if (existing != null) {
            Button btnDelete = new Button("🗑 Hapus Tugas");
            btnDelete.setStyle(
                    "-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-background-radius: 6;");
            btnDelete.setOnAction(e -> {
                personalTaskDAO.deleteTask(existing.getId());
                dialog.setResult(null);
                dialog.close();
                loadTasks();
            });
            grid.add(btnDelete, 1, row++);
        }

        Label validLbl = new Label();
        validLbl.setStyle("-fx-text-fill: #ef4444;");
        grid.add(validLbl, 1, row);
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(titleField::requestFocus);

        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (titleField.getText().isBlank() || deadlinePicker.getValue() == null) {
                validLbl.setText("Judul dan Deadline harus diisi!");
                event.consume();
            }
        });

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                User user = AuthService.getCurrentUser();
                PersonalTask task = existing != null ? existing : new PersonalTask();
                task.setTitle(titleField.getText().trim());
                task.setDescription(descField.getText().trim());
                
                // --- KUNCI: MENGGABUNGKAN KATEGORI DAN TIPE SEBELUM DISIMPAN ---
                String finalCategory = categoryBox.getValue() + " • " + typeBox.getValue();
                task.setCategory(finalCategory);
                
                task.setStatus(statusBox.getValue());
                task.setDeadline(deadlinePicker.getValue());

                long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), deadlinePicker.getValue());
                if (daysLeft <= 2)
                    task.setPriority("High");
                else if (daysLeft <= 7)
                    task.setPriority("Medium");
                else
                    task.setPriority("Low");

                if (user != null)
                    task.setUserId(user.getId());
                return task;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(task -> {
            boolean success = existing == null ? personalTaskDAO.addTask(task) : personalTaskDAO.updateTask(task);
            if (success)
                loadTasks();
        });
    }
}