package com.taskflow.controller;

import com.taskflow.dao.PersonalTaskDAO;
import com.taskflow.dao.TaskDAO;
import com.taskflow.model.User;
import com.taskflow.service.AuthService;
import com.taskflow.service.TaskService;
import com.taskflow.util.SceneManager;
import com.taskflow.view.StatisticsView;
import javafx.collections.FXCollections;
import javafx.scene.chart.*;

import java.util.List;
import java.util.Map;

public class StatisticsController {

    private final StatisticsView view;
    private final TaskService taskService = new TaskService();
    private final TaskDAO taskDAO = new TaskDAO();
    private final PersonalTaskDAO personalTaskDAO = new PersonalTaskDAO();

    public StatisticsController(StatisticsView view) {
        this.view = view;
    }

    public void initialize() {
        User user = AuthService.getCurrentUser();
        Integer userId = (user != null && !user.isManager()) ? user.getId() : null;

        view.pageTitle.setText(user != null && user.isManager()
            ? "Statistik Tim — " + user.getFullName()
            : "Statistik Pribadi — " + (user != null ? user.getFullName() : ""));

        // Wire back buttons
        view.backBtn.setOnAction(e -> SceneManager.switchTo("dashboard"));
        view.btnBack.setOnAction(e -> SceneManager.switchTo("dashboard"));

        // Load project filter
        List<String> projects = taskDAO.getDistinctProjects();
        view.projectFilter.setItems(FXCollections.observableArrayList(projects));
        view.projectFilter.setValue("Semua Proyek");
        view.projectFilter.setOnAction(e -> loadCharts(userId));

        loadKpis(userId);
        loadCharts(userId);
    }

    private void loadKpis(Integer userId) {
        view.kpiTotal.setText(String.valueOf(taskService.getTotal(userId)));
        view.kpiDone.setText(String.valueOf(taskService.getDone(userId)));
        view.kpiInProgress.setText(String.valueOf(taskService.getInProgress(userId)));
        view.kpiOverdue.setText(String.valueOf(taskService.getOverdue(userId)));
    }

    private void loadCharts(Integer userId) {
        loadPieChart(userId);
        loadBarChart();
        if (userId != null) {
            loadPersonalPieChart(userId);
            loadPersonalBarChart(userId);
        }
    }

    private void loadPieChart(Integer userId) {
        int todo = taskService.getToDo(userId);
        int inProgress = taskService.getInProgress(userId);
        int done = taskService.getDone(userId);

        view.pieChart.getData().clear();
        if (todo + inProgress + done == 0) { view.pieChart.setTitle("Tidak ada data"); return; }

        PieChart.Data sliceTodo = new PieChart.Data("To Do (" + todo + ")", todo);
        PieChart.Data sliceInProgress = new PieChart.Data("In Progress (" + inProgress + ")", inProgress);
        PieChart.Data sliceDone = new PieChart.Data("Done (" + done + ")", done);

        view.pieChart.getData().addAll(sliceTodo, sliceInProgress, sliceDone);
        view.pieChart.setTitle("Status Tugas Kelompok");

        javafx.application.Platform.runLater(() -> {
            if (sliceTodo.getNode() != null) sliceTodo.getNode().setStyle("-fx-pie-color: #94a3b8;");
            if (sliceInProgress.getNode() != null) sliceInProgress.getNode().setStyle("-fx-pie-color: #3b82f6;");
            if (sliceDone.getNode() != null) sliceDone.getNode().setStyle("-fx-pie-color: #10b981;");
        });
    }

    private void loadBarChart() {
        String filter = view.projectFilter.getValue();
        Map<String, Map<String, Integer>> workload = taskDAO.getWorkloadData(filter);

        view.barChart.getData().clear();
        view.barXAxis.setLabel("Anggota Tim");
        view.barYAxis.setLabel("Jumlah Tugas");

        XYChart.Series<String, Number> todoSeries = new XYChart.Series<>();
        todoSeries.setName("To Do");
        XYChart.Series<String, Number> inProgressSeries = new XYChart.Series<>();
        inProgressSeries.setName("In Progress");

        for (Map.Entry<String, Map<String, Integer>> entry : workload.entrySet()) {
            String member = entry.getKey();
            Map<String, Integer> statusMap = entry.getValue();
            String shortName = member.contains(" ") ? member.substring(0, member.indexOf(" ")) : member;
            todoSeries.getData().add(new XYChart.Data<>(shortName, statusMap.getOrDefault("To Do", 0)));
            inProgressSeries.getData().add(new XYChart.Data<>(shortName, statusMap.getOrDefault("In Progress", 0)));
        }

        if (!workload.isEmpty()) view.barChart.getData().addAll(todoSeries, inProgressSeries);

        javafx.application.Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : view.barChart.getData()) {
                String color = series.getName().equals("To Do") ? "#94a3b8" : "#3b82f6";
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) data.getNode().setStyle("-fx-bar-fill: " + color + ";");
                }
            }
        });
    }

    private void loadPersonalPieChart(int userId) {
        int todo = personalTaskDAO.countByStatus("To Do", userId);
        int inProgress = personalTaskDAO.countByStatus("In Progress", userId);
        int done = personalTaskDAO.countByStatus("Done", userId);

        view.personalPieChart.getData().clear();
        if (todo + inProgress + done == 0) { view.personalPieChart.setTitle("Tidak ada data"); return; }

        PieChart.Data sliceTodo = new PieChart.Data("To Do (" + todo + ")", todo);
        PieChart.Data sliceInProgress = new PieChart.Data("In Progress (" + inProgress + ")", inProgress);
        PieChart.Data sliceDone = new PieChart.Data("Done (" + done + ")", done);

        view.personalPieChart.getData().addAll(sliceTodo, sliceInProgress, sliceDone);
        view.personalPieChart.setTitle("Status Tugas Mandiri");

        javafx.application.Platform.runLater(() -> {
            if (sliceTodo.getNode() != null) sliceTodo.getNode().setStyle("-fx-pie-color: #f59e0b;");
            if (sliceInProgress.getNode() != null) sliceInProgress.getNode().setStyle("-fx-pie-color: #8b5cf6;");
            if (sliceDone.getNode() != null) sliceDone.getNode().setStyle("-fx-pie-color: #10b981;");
        });
    }

    private void loadPersonalBarChart(int userId) {
        Map<String, Map<String, Integer>> categoryData = personalTaskDAO.getCategoryData(userId);

        view.personalBarChart.getData().clear();
        view.personalBarXAxis.setLabel("Kategori");
        view.personalBarYAxis.setLabel("Jumlah Tugas");

        XYChart.Series<String, Number> todoSeries = new XYChart.Series<>(); todoSeries.setName("To Do");
        XYChart.Series<String, Number> inProgressSeries = new XYChart.Series<>(); inProgressSeries.setName("In Progress");
        XYChart.Series<String, Number> doneSeries = new XYChart.Series<>(); doneSeries.setName("Done");

        for (Map.Entry<String, Map<String, Integer>> entry : categoryData.entrySet()) {
            String cat = entry.getKey();
            Map<String, Integer> statusMap = entry.getValue();
            todoSeries.getData().add(new XYChart.Data<>(cat, statusMap.getOrDefault("To Do", 0)));
            inProgressSeries.getData().add(new XYChart.Data<>(cat, statusMap.getOrDefault("In Progress", 0)));
            doneSeries.getData().add(new XYChart.Data<>(cat, statusMap.getOrDefault("Done", 0)));
        }

        if (!categoryData.isEmpty()) view.personalBarChart.getData().addAll(todoSeries, inProgressSeries, doneSeries);

        javafx.application.Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : view.personalBarChart.getData()) {
                String color = switch (series.getName()) {
                    case "To Do" -> "#f59e0b";
                    case "In Progress" -> "#8b5cf6";
                    default -> "#10b981";
                };
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) data.getNode().setStyle("-fx-bar-fill: " + color + ";");
                }
            }
        });
    }
}
