package org.itmo.carpet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CarpetApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CarpetApplication.class.getResource("carpet-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Генератор ковра Серпинского");
        stage.setScene(scene);
        stage.setOnCloseRequest(windowEvent -> System.exit(0));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}