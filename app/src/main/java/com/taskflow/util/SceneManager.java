package com.taskflow.util;

import com.taskflow.controller.DashboardController;
import com.taskflow.controller.LoginController;
import com.taskflow.controller.StatisticsController;
import com.taskflow.view.DashboardView;
import com.taskflow.view.LoginView;
import com.taskflow.view.StatisticsView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage primaryStage;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String sceneName) {
        try {
            javafx.scene.Parent root = switch (sceneName.toLowerCase()) {
                case "login" -> buildLogin();
                case "dashboard" -> buildDashboard();
                case "statistics" -> buildStatistics();
                default -> throw new IllegalArgumentException("Unknown scene: " + sceneName);
            };

            if (primaryStage.getScene() == null) {
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                primaryStage.getScene().setRoot(root);
            }

        } catch (Exception e) {
            System.err.println("[SceneManager] Error switching to scene '" + sceneName + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static LoginView buildLogin() {
        LoginView view = new LoginView();
        LoginController controller = new LoginController(view);
        controller.initialize();
        return view;
    }

    private static DashboardView buildDashboard() {
        DashboardView view = new DashboardView();
        DashboardController controller = new DashboardController(view);
        controller.initialize();
        return view;
    }

    private static StatisticsView buildStatistics() {
        StatisticsView view = new StatisticsView();
        StatisticsController controller = new StatisticsController(view);
        controller.initialize();
        return view;
    }

    public static Stage getStage() {
        return primaryStage;
    }
}