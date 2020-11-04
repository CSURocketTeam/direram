package com.kmschr.direram.ui;

import com.kmschr.direram.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class GroundStationUI extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1400, 800);
            stage.setTitle("Aries CoVId Ground Station");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/rocket.png")));
            stage.setScene(scene);
            stage.show();
            Client.getInstance().setPrimaryStage(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void entry() {
        launch();
    }

}
