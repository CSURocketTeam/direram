package com.kmschr.direram.controllers;

import com.kmschr.direram.Client;
import com.sothawo.mapjfx.MapType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TileServerController implements Initializable {

    @FXML private AnchorPane anchorPane;
    @FXML private ComboBox<String> serverType;
    @FXML private TextField url;

    @FXML private Button applyButton;
    @FXML private Button cancelButton;

    private Client client;

    public TileServerController() {
        this.client = Client.getInstance();
    }

    public void apply() {
        client.setTileServerURL(url.getText());

        System.out.println(serverType.getValue());

        switch (serverType.getValue()) {
            case "XYZ Server":
                client.setMapType(MapType.XYZ);
                break;
            case "WMS Server":
                client.setMapType(MapType.WMS);
            case "Bing Maps":
                client.setMapType(MapType.BINGMAPS_ROAD);
                break;
            case "Open Street Maps":
                client.setMapType(MapType.OSM);
                break;
            default:
                break;
        }

        client.refreshMap();
        close();
    }

    public void close() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        anchorPane.requestFocus();
        serverType.getItems().setAll("XYZ Server", "WMS Server", "Bing Maps", "Open Street Maps");
        serverType.setValue("XYZ Server");

        cancelButton.setOnAction(actionEvent -> close());
        applyButton.setOnAction(actionEvent -> apply());
    }
}
