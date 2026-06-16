package com.taskflow.controller;

import com.taskflow.dao.PersonalTaskDAO;
import com.taskflow.model.PersonalTask;
import com.taskflow.model.User;
import com.taskflow.service.AuthService;
import com.taskflow.util.SceneManager;
import com.taskflow.view.StatisticsView;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsController {

    private final StatisticsView view;
    private final PersonalTaskDAO personalTaskDAO = new PersonalTaskDAO();

    public StatisticsController(StatisticsView view) {
        this.view = view;
    }

    public void initialize() {
        User user = AuthService.getCurrentUser();
        view.welcomeLabel.setText(user != null ? user.getFullName() : "Pengguna");

        view.btnDashboard.setOnAction(e -> SceneManager.switchTo("dashboard"));
        view.btnLogout.setOnAction(e -> SceneManager.switchTo("login"));

        if (user != null) {
            loadData(user.getId());
        }
    }

    private void loadData(int userId) {
        List<PersonalTask> tasks = personalTaskDAO.getTasksByUser(userId);

        int total = tasks.size();
        int done = (int) tasks.stream().filter(t -> "Done".equals(t.getStatus())).count();
        int inProgress = (int) tasks.stream().filter(t -> "In Progress".equals(t.getStatus())).count();
        int overdue = (int) tasks.stream().filter(PersonalTask::isOverdue).count();

        view.totalTaskLabel.setText(String.valueOf(total));
        view.inProgressTaskLabel.setText(String.valueOf(inProgress));
        view.completedTaskLabel.setText(String.valueOf(done));
        view.overdueTaskLabel.setText(String.valueOf(overdue));

        view.statusPieChart.getData().clear();
        if (total > 0) {
            int todo = total - done - inProgress;
            PieChart.Data sliceTodo = new PieChart.Data("To Do (" + todo + ")", todo);
            PieChart.Data sliceInProgress = new PieChart.Data("In Progress (" + inProgress + ")", inProgress);
            PieChart.Data sliceDone = new PieChart.Data("Done (" + done + ")", done);

            view.statusPieChart.getData().addAll(sliceTodo, sliceInProgress, sliceDone);

            javafx.application.Platform.runLater(() -> {
                if (sliceTodo.getNode() != null) sliceTodo.getNode().setStyle("-fx-pie-color: #94a3b8;");
                if (sliceInProgress.getNode() != null) sliceInProgress.getNode().setStyle("-fx-pie-color: #f59e0b;");
                if (sliceDone.getNode() != null) sliceDone.getNode().setStyle("-fx-pie-color: #10b981;");
            });
        } else {
            view.statusPieChart.setTitle("Belum ada data");
        }

        view.categoryBarChart.getData().clear();
        
        Map<String, Map<String, Integer>> cleanCategoryData = new HashMap<>();

        for (PersonalTask task : tasks) {
            String rawCategory = task.getCategory();
            String status = task.getStatus() == null ? "To Do" : task.getStatus();
            String cleanCategory = rawCategory;
            if (rawCategory != null && rawCategory.contains(" • ")) {
                cleanCategory = rawCategory.split(" • ")[0].trim();
            } else if (rawCategory == null || rawCategory.trim().isEmpty()){
                cleanCategory = "General";
            }

            cleanCategoryData.putIfAbsent(cleanCategory, new HashMap<>());
            Map<String, Integer> statusCount = cleanCategoryData.get(cleanCategory);
            statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
        }

        XYChart.Series<String, Number> todoSeries = new XYChart.Series<>();
        todoSeries.setName("To Do");
        XYChart.Series<String, Number> inProgressSeries = new XYChart.Series<>();
        inProgressSeries.setName("In Progress");
        XYChart.Series<String, Number> doneSeries = new XYChart.Series<>();
        doneSeries.setName("Done");

        for (Map.Entry<String, Map<String, Integer>> entry : cleanCategoryData.entrySet()) {
            String cat = entry.getKey();
            Map<String, Integer> statusMap = entry.getValue();
            todoSeries.getData().add(new XYChart.Data<>(cat, statusMap.getOrDefault("To Do", 0)));
            inProgressSeries.getData().add(new XYChart.Data<>(cat, statusMap.getOrDefault("In Progress", 0)));
            doneSeries.getData().add(new XYChart.Data<>(cat, statusMap.getOrDefault("Done", 0)));
        }

        if (!cleanCategoryData.isEmpty()) {
            view.categoryBarChart.getData().addAll(todoSeries, inProgressSeries, doneSeries);
            
            javafx.application.Platform.runLater(() -> {
                if (view.categoryBarChart instanceof BarChart) {
                    BarChart<String, Number> bc = (BarChart<String, Number>) view.categoryBarChart;
                    bc.setCategoryGap(40); 
                    bc.setBarGap(0);       
                }

                for (XYChart.Series<String, Number> series : view.categoryBarChart.getData()) {
                    String color = switch (series.getName()) {
                        case "To Do" -> "#94a3b8";
                        case "In Progress" -> "#f59e0b";
                        default -> "#10b981";
                    };
                    for (XYChart.Data<String, Number> data : series.getData()) {
                        if (data.getNode() != null) {
                            data.getNode().setStyle("-fx-bar-fill: " + color + ";");
                        }
                    }
                }

                int legendIndex = 0;
                for (Node node : view.categoryBarChart.lookupAll(".chart-legend-item-symbol")) {
                    if (legendIndex == 0) node.setStyle("-fx-background-color: #94a3b8;");
                    else if (legendIndex == 1) node.setStyle("-fx-background-color: #f59e0b;");
                    else if (legendIndex == 2) node.setStyle("-fx-background-color: #10b981;");
                    legendIndex++;
                }
            });
        }
    }
}