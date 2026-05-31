package com.taskflow;

import com.taskflow.config.DatabaseConfig;
import com.taskflow.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseConfig.initialize();
        SceneManager.init(primaryStage);
        SceneManager.switchTo("login");
        primaryStage.setTitle("TaskFlow - Manajemen Tugas");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
