package com.kmschr.direram;

import com.kmschr.direram.aprs.APRSPacket;
import com.kmschr.direram.aprs.PacketLog;
import com.kmschr.direram.controllers.MainController;
import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.MapType;
import com.sothawo.mapjfx.Marker;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Client {

    private static Client instance;

    private PacketLog packetLog;

    private MainController mainController;

    private String tileServerURL = "http://localhost:8080/styles/basic-preview/{z}/{x}/{y}.png";
    private String webcamStreamURL = "http://192.168.0.229/HLSStream/webcam0.m3u8";
    private MapType mapType = MapType.OSM;

    private Client() {
        packetLog = new PacketLog();
    }

    public void setTileServerURL(String url) {
        tileServerURL = url;
    }

    public void setWebcamStreamURL(String url) {
        webcamStreamURL = url;
    }

    public void makeStreamResizeable(Stage stage) {
        stage.heightProperty().addListener((obs, oldVal, newVal) -> mainController.resizeStream(oldVal, newVal));
    }

    public void setMainController(MainController vc) {
        this.mainController = vc;
    }

    public void setMapType(MapType mt) {
        this.mapType = mt;
    }

    public String getTileServerURL() {
        return tileServerURL;
    }

    public String getWebcamStreamURL() {
        return webcamStreamURL;
    }

    public MapType getMapType() {
        return mapType;
    }

    public void parsePacket(String packet) {
        println(packet);
        APRSPacket aprsPacket = new APRSPacket(packet);

        // Only parse APRS family of packets
        if (!aprsPacket.isAPRS())
            return;

        // Don't parse packets without position information
        Coordinate pos = aprsPacket.getPosition();
        if (pos == null)
            return;

        packetLog.addPacket(aprsPacket);

        // Update UI with packet info
        Platform.runLater(() -> {
            mainController.setMapCenter(pos);
            Marker marker = new Marker(getClass().getResource("img/marker.png"), -32, -32).setPosition(pos).setVisible(true);
            mainController.addMapMarker(marker);
            mainController.setStatus(pos, aprsPacket.getPositionString(), aprsPacket.getAltitude(), aprsPacket.getCourse(), aprsPacket.getSpeed());
        });
    }

    public void showKMLDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export KML");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("KML Files", "*.kml"));
        fileChooser.setInitialFileName("launch-1.kml");
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile == null)
            return;

        println("> Exporting to " + selectedFile.getName());
        packetLog.writeKML(selectedFile);
    }

    public void showCSVDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("launch-1.csv");
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile == null)
            return;

        println("> Exporting to " + selectedFile.getName());
        packetLog.writeCSV(selectedFile);
    }

    public void showTileServerPrompt() {
        Stage stage = new Stage();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/tileServer.fxml"));
            Scene scene = new Scene(root, 400, 118);
            stage.setTitle("Select Tile Server...");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/rocket.png")));
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showWebcamStreamPrompt() {
        Stage stage = new Stage();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/webcamServer.fxml"));
            Scene scene = new Scene(root, 400, 118);
            stage.setTitle("Select Webcam Stream...");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/rocket.png")));
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshMap() {
        mainController.refreshMap();
    }

    public void refreshStream() {
        mainController.refreshStream();
    }

    public void println(String s) {
        Platform.runLater(() -> mainController.addLine(s));
    }

    public static Client getInstance() {
        if (instance == null)
            instance = new Client();
        return instance;
    }

}
