package com.kmschr.direram.controllers;

import com.kmschr.direram.Client;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class WebcamController implements Initializable {

    @FXML private AnchorPane anchorPane;
    @FXML private TextField url;

    @FXML private Button applyButton;
    @FXML private Button cancelButton;

    private Client client;

    public WebcamController() {
        this.client = Client.getInstance();
    }

    public void apply() {
        client.setWebcamStreamURL(url.getText());
        client.refreshStream();
        close();
    }

    public void close() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        anchorPane.requestFocus();
        cancelButton.setOnAction(actionEvent -> close());
        applyButton.setOnAction(actionEvent -> apply());
    }
}
