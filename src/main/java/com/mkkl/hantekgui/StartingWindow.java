package com.mkkl.hantekgui;

import com.mkkl.hantekgui.protocol.HantekCommunication;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;
import com.mkkl.hantekgui.protocol.OscilloscopeDevice;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StartingWindow extends Application {
    @FXML public ComboBox<OscilloscopeDevice> availableOscilloscopes;
    @FXML public Button startButton;

    private OscilloscopeCommunication scopeCommunication;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(StartingWindow.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Start");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }

    @FXML
    protected void initialize() {
        scopeCommunication = new HantekCommunication();
        Callback<ListView<OscilloscopeDevice>, ListCell<OscilloscopeDevice>> factory = x -> new ListCell<OscilloscopeDevice>() {
            @Override
            protected void updateItem(OscilloscopeDevice item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "error" : item.name());
            }
        };
        availableOscilloscopes.setCellFactory(factory);
        availableOscilloscopes.setButtonCell(factory.call(null));

        try {
            List<OscilloscopeDevice> deviceList = new ArrayList<>(scopeCommunication.getConnectedDevices());
            availableOscilloscopes.setItems(FXCollections.observableList(deviceList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStartButtonClicked(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            Stage stage = new Stage();
            stage.setTitle("Hello!");
            stage.setScene(scene);
            MainWindow mainWindow = fxmlLoader.getController();
            scopeCommunication.connectDevice(availableOscilloscopes.getValue());
            mainWindow.init(scopeCommunication);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
