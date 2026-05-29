package com.taskflow.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * SceneManager - manages scene transitions
 */
public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                SceneManager.class.getResource(fxmlPath)
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);

            String cssPath = "/com/taskflow/css/style.css";
            if (SceneManager.class.getResource(cssPath) != null) {
                scene.getStylesheets().add(
                    SceneManager.class.getResource(cssPath).toExternalForm()
                );
            }

            primaryStage.setTitle("TaskFlow - " + title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void switchToLogin() {
        switchScene("/com/taskflow/view/login.fxml", "Login");
    }

    public static void switchToDashboard() {
        switchScene("/com/taskflow/view/dashboard.fxml", "Dashboard");
    }

    public static void switchToStatistics() {
        switchScene("/com/taskflow/view/statistics.fxml", "Statistik");
    }
}
