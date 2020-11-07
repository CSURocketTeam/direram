package com.kmschr.direram.controllers;

import com.kmschr.direram.Client;
import com.kmschr.direram.kiss.DireWolfRunner;
import com.sothawo.mapjfx.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private GridPane gridPane;
    @FXML private TextArea packetField;
    @FXML private TextArea statusField;
    @FXML private MapView mapView;
    @FXML private MediaView mediaView;

    @FXML private MenuItem refreshStream;
    @FXML private MenuItem clearConsole;
    @FXML private MenuItem clearMap;
    @FXML private MenuItem exportKML;
    @FXML private MenuItem exportCSV;

    @FXML private MenuItem tileServer;
    @FXML private MenuItem webcamStream;

    private final Client client;
    private Marker curMarker;
    private List<CoordinateLine> lines;
    private int max_alt = 0;

    public MainController() {
        this.client = Client.getInstance();
        this.client.setMainController(this);
        this.curMarker = null;
        this.lines = new ArrayList<>();
    }

    public void addLine(String text) {
        this.packetField.appendText(text + "\n");
    }

    public void setStatus(Coordinate position, String pos, int altitude, int course, int speed) {
        if (altitude > max_alt)
            max_alt = altitude;
        this.statusField.setText("""
                Position:
                  %s
                  %s
                Altitude: %d ft
                Course: %d\u00B0
                Speed: %d knots
                Max Altitude: %d ft""");
        this.statusField.setText("Position: \n" +
                                 "  " + position.getLatitude() + ", " + position.getLongitude() + "\n" +
                                 "  " + pos + "\n" +
                                 "Altitude: " + (altitude == -1 ? "?" : altitude) + " ft\n" +
                                 "Course: " + (course == -1 ? "?" : course) + "\u00B0\n" +
                                 "Speed: " + (speed == -1 ? "?" : speed) + " knots\n" +
                                 "Max Altitude: " + max_alt + " ft\n");
        ScrollBar sbv = (ScrollBar) statusField.lookup(".scroll-bar:vertical");
        if (sbv != null)
            sbv.setDisable(true);
    }

    public void clearConsole() {
        packetField.setText("");
    }

    public void clearMap() {
        mapView.removeMarker(curMarker);
        for (CoordinateLine line : lines) {
            mapView.removeCoordinateLine(line);
        }
        curMarker = null;
        lines = new ArrayList<>();
    }

    public void refreshStream() {
        MediaPlayer mp1 = new MediaPlayer(new Media(client.getWebcamStreamURL()));
        mp1.play();
        mediaView.setMediaPlayer(mp1);
    }

    public void refreshMap() {
        switch (client.getMapType()) {
            case XYZ -> mapView.setXYZParam(new XYZParam().withUrl(client.getTileServerURL()));
            case WMS -> mapView.setWMSParam(new WMSParam().setUrl(client.getTileServerURL()));
            case BINGMAPS_ROAD -> mapView.setBingMapsApiKey(client.getTileServerURL());
            default -> {}
        }
        mapView.setMapType(client.getMapType());
        Projection projection = Projection.WEB_MERCATOR;
        mapView.initialize(Configuration.builder()
                .projection(projection)
                .showZoomControls(true)
                .build());
        mapView.setZoom(12);
        mapView.setCenter(new Coordinate(40.551229, -105.094120));
    }

    public void setMapCenter(Coordinate pos) {
        mapView.setCenter(pos);
    }

    public void addMapMarker(Marker marker) {
        if (curMarker != null) {
            List<Coordinate> coords = new ArrayList<>();
            coords.add(curMarker.getPosition());
            coords.add(marker.getPosition());
            CoordinateLine newLine = new CoordinateLine(coords).setColor(Color.DODGERBLUE);
            mapView.addCoordinateLine(newLine);
            newLine.setVisible(true);
            lines.add(newLine);
            curMarker.setVisible(false);
        }
        curMarker = marker;
        mapView.addMarker(curMarker);
        curMarker.setVisible(true);
    }

    public void resizeStream(Number oldVal, Number newVal) {
        double resizeRatio = newVal.doubleValue() / oldVal.doubleValue();
        mediaView.setFitHeight(mediaView.getFitHeight() * resizeRatio);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mediaView.fitWidthProperty().bind(((GridPane) mediaView.getParent()).widthProperty());
        refreshStream();
        refreshMap();
        gridPane.setOnMouseClicked(actionEvent -> gridPane.requestFocus());
        refreshStream.setOnAction(actionEvent -> refreshStream());
        //refreshMap.setOnAction(actionEvent -> refreshMap());
        clearConsole.setOnAction(actionEvent -> clearConsole());
        clearMap.setOnAction(actionEvent -> clearMap());
        exportKML.setOnAction(actionEvent -> client.showKMLDialog());
        exportCSV.setOnAction(actionEvent -> client.showCSVDialog());
        tileServer.setOnAction(actionEvent -> client.showTileServerPrompt());
        webcamStream.setOnAction(actionEvent -> client.showWebcamStreamPrompt());
        new Thread(new DireWolfRunner()).start();
    }
}
