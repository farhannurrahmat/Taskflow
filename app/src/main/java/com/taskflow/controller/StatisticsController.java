package com.taskflow.controller;

import com.taskflow.dao.PersonalTaskDAO;
import com.taskflow.dao.TaskDAO;
import com.taskflow.model.User;
import com.taskflow.service.AuthService;
import com.taskflow.service.TaskService;
import com.taskflow.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.List;
import java.util.Map;

public class StatisticsController {

    @FXML private Label kpiTotal;
    @FXML private Label kpiDone;
    @FXML private Label kpiOverdue;
    @FXML private Label kpiInProgress;
    @FXML private PieChart pieChart;
    @FXML private StackedBarChart<String, Number> barChart;
    @FXML private CategoryAxis barXAxis;
    @FXML private NumberAxis barYAxis;
    @FXML private ComboBox<String> projectFilter;
    @FXML private Label pageTitle;

    // Personal task charts
    @FXML private PieChart personalPieChart;
    @FXML private BarChart<String, Number> personalBarChart;
    @FXML private CategoryAxis personalBarXAxis;
    @FXML private NumberAxis personalBarYAxis;

    private final TaskService taskService = new TaskService();
    private final TaskDAO taskDAO = new TaskDAO();
    private final PersonalTaskDAO personalTaskDAO = new PersonalTaskDAO();

    @FXML
    public void initialize() {
        User user = AuthService.getCurrentUser();
        Integer userId = (user != null && !user.isManager()) ? user.getId() : null;

        pageTitle.setText(user != null && user.isManager()
            ? "Statistik Tim - " + user.getFullName()
            : "Statistik Pribadi - " + (user != null ? user.getFullName() : ""));

        // Load project filter
        List<String> projects = taskDAO.getDistinctProjects();
        projectFilter.setItems(FXCollections.observableArrayList(projects));
        projectFilter.setValue("Semua Proyek");
        projectFilter.setOnAction(e -> loadCharts(userId));

        loadKpis(userId);
        loadCharts(userId);
    }

    private void loadKpis(Integer userId) {
        int total = taskService.getTotal(userId);
        int done = taskService.getDone(userId);
        int inProgress = taskService.getInProgress(userId);
        int overdue = taskService.getOverdue(userId);

        kpiTotal.setText(String.valueOf(total));
        kpiDone.setText(String.valueOf(done));
        kpiOverdue.setText(String.valueOf(overdue));
        kpiInProgress.setText(String.valueOf(inProgress));
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

        pieChart.getData().clear();
        if (todo + inProgress + done == 0) {
            pieChart.setTitle("Tidak ada data");
            return;
        }

        PieChart.Data sliceTodo = new PieChart.Data("To Do (" + todo + ")", todo);
        PieChart.Data sliceInProgress = new PieChart.Data("In Progress (" + inProgress + ")", inProgress);
        PieChart.Data sliceDone = new PieChart.Data("Done (" + done + ")", done);

        pieChart.getData().addAll(sliceTodo, sliceInProgress, sliceDone);
        pieChart.setTitle("Status Tugas Kelompok");
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);

        javafx.application.Platform.runLater(() -> {
            if (sliceTodo.getNode() != null)
                sliceTodo.getNode().setStyle("-fx-pie-color: #95a5a6;");
            if (sliceInProgress.getNode() != null)
                sliceInProgress.getNode().setStyle("-fx-pie-color: #3498db;");
            if (sliceDone.getNode() != null)
                sliceDone.getNode().setStyle("-fx-pie-color: #2ecc71;");
        });
    }

    private void loadBarChart() {
        String filter = projectFilter.getValue();
        Map<String, Map<String, Integer>> workload = taskDAO.getWorkloadData(filter);

        barChart.getData().clear();
        barXAxis.setLabel("Anggota Tim");
        barYAxis.setLabel("Jumlah Tugas");
        barChart.setTitle("Beban Kerja Tim");

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

        if (!workload.isEmpty()) {
            barChart.getData().addAll(todoSeries, inProgressSeries);
        }

        javafx.application.Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : barChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        String color = series.getName().equals("To Do") ? "#95a5a6" : "#3498db";
                        data.getNode().setStyle("-fx-bar-fill: " + color + ";");
                    }
                }
            }
        });
    }

    private void loadPersonalPieChart(int userId) {
        if (personalPieChart == null) return;
        int todo = personalTaskDAO.countByStatus("To Do", userId);
        int inProgress = personalTaskDAO.countByStatus("In Progress", userId);
        int done = personalTaskDAO.countByStatus("Done", userId);

        personalPieChart.getData().clear();
        if (todo + inProgress + done == 0) {
            personalPieChart.setTitle("Tidak ada data");
            return;
        }

        PieChart.Data sliceTodo = new PieChart.Data("To Do (" + todo + ")", todo);
        PieChart.Data sliceInProgress = new PieChart.Data("In Progress (" + inProgress + ")", inProgress);
        PieChart.Data sliceDone = new PieChart.Data("Done (" + done + ")", done);

        personalPieChart.getData().addAll(sliceTodo, sliceInProgress, sliceDone);
        personalPieChart.setTitle("Status Tugas Mandiri");
        personalPieChart.setLegendVisible(true);
        personalPieChart.setLabelsVisible(true);

        javafx.application.Platform.runLater(() -> {
            if (sliceTodo.getNode() != null)
                sliceTodo.getNode().setStyle("-fx-pie-color: #f39c12;");
            if (sliceInProgress.getNode() != null)
                sliceInProgress.getNode().setStyle("-fx-pie-color: #9b59b6;");
            if (sliceDone.getNode() != null)
                sliceDone.getNode().setStyle("-fx-pie-color: #1abc9c;");
        });
    }

    private void loadPersonalBarChart(int userId) {
        if (personalBarChart == null) return;
        Map<String, Map<String, Integer>> categoryData = personalTaskDAO.getCategoryData(userId);

        personalBarChart.getData().clear();
        personalBarXAxis.setLabel("Kategori");
        personalBarYAxis.setLabel("Jumlah Tugas");
        personalBarChart.setTitle("Tugas Mandiri per Kategori");

        XYChart.Series<String, Number> todoSeries = new XYChart.Series<>();
        todoSeries.setName("To Do");
        XYChart.Series<String, Number> inProgressSeries = new XYChart.Series<>();
        inProgressSeries.setName("In Progress");
        XYChart.Series<String, Number> doneSeries = new XYChart.Series<>();
        doneSeries.setName("Done");

        for (Map.Entry<String, Map<String, Integer>> entry : categoryData.entrySet()) {
            String cat = entry.getKey();
            Map<String, Integer> statusMap = entry.getValue();
            todoSeries.getData().add(new XYChart.Data<>(cat, statusMap.getOrDefault("To Do", 0)));
            inProgressSeries.getData().add(new XYChart.Data<>(cat, statusMap.getOrDefault("In Progress", 0)));
            doneSeries.getData().add(new XYChart.Data<>(cat, statusMap.getOrDefault("Done", 0)));
        }

        if (!categoryData.isEmpty()) {
            personalBarChart.getData().addAll(todoSeries, inProgressSeries, doneSeries);
        }

        javafx.application.Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : personalBarChart.getData()) {
                String color = switch (series.getName()) {
                    case "To Do" -> "#f39c12";
                    case "In Progress" -> "#9b59b6";
                    default -> "#1abc9c";
                };
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        data.getNode().setStyle("-fx-bar-fill: " + color + ";");
                    }
                }
            }
        });
    }

    @FXML
    private void handleBack() {
        SceneManager.switchTo("dashboard");
    }
}
