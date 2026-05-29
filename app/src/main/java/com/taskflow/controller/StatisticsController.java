package com.taskflow.controller;

import com.taskflow.service.TaskService;
import com.taskflow.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class StatisticsController {
    @FXML private Label totalLabel;
    @FXML private Label selesaiLabel;
    @FXML private Label belumLabel;
    @FXML private Label progressLabel;
    @FXML private ProgressBar progressBar;

    private TaskService taskService = TaskService.getInstance();

    @FXML
    public void initialize() {
        int total = taskService.getTotalTasks();
        int selesai = taskService.getCompletedTasks();
        int belum = taskService.getPendingTasks();
        double pct = taskService.getProgressPercentage();

        totalLabel.setText(String.valueOf(total));
        selesaiLabel.setText(String.valueOf(selesai));
        belumLabel.setText(String.valueOf(belum));
        progressLabel.setText(String.format("%.1f%%", pct));
        progressBar.setProgress(pct / 100.0);
    }

    @FXML
    private void handleBack() {
        SceneManager.switchToDashboard();
    }
}
