package com.taskflow.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private static Stage primaryStage;
    private static final Map<String, String> sceneMap = new HashMap<>();

    static {
        sceneMap.put("login", "/view/login.fxml");
        sceneMap.put("dashboard", "/view/dashboard.fxml");
        sceneMap.put("statistics", "/view/statistics.fxml");
    }

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String sceneName) {
        try {
            String fxmlPath = sceneMap.get(sceneName);
            if (fxmlPath == null) throw new IllegalArgumentException("Unknown scene: " + sceneName);

            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

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

    public static Stage getStage() {
        return primaryStage;
    }
}
