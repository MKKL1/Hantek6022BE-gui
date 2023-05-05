package com.mkkl.hantekgui;

import com.mkkl.hantekgui.protocol.HantekCommunication;
import com.mkkl.hantekgui.protocol.AbstractProtocol;
import com.mkkl.hantekgui.protocol.AbstractDevice;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class StartingWindow extends Application {
    @FXML public ComboBox<AbstractDevice> availableOscilloscopes;
    @FXML public Button startButton;

    private AbstractProtocol scopeCommunication;

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
        Callback<ListView<AbstractDevice>, ListCell<AbstractDevice>> factory = x -> new ListCell<AbstractDevice>() {
            @Override
            protected void updateItem(AbstractDevice item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "error" : item.name());
            }
        };
        availableOscilloscopes.setCellFactory(factory);
        availableOscilloscopes.setButtonCell(factory.call(null));

        try {
            List<AbstractDevice> deviceList = new ArrayList<>(scopeCommunication.getConnectedDevices());
            availableOscilloscopes.setItems(FXCollections.observableList(deviceList));
            availableOscilloscopes.setValue(deviceList.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStartButtonClicked(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            Stage stage = new Stage();
            stage.setTitle("Hello!");
            stage.setScene(scene);
            MainWindow mainWindow = fxmlLoader.getController();
            scopeCommunication.connectDevice(availableOscilloscopes.getValue());
            mainWindow.init(scopeCommunication);

            stage.setOnCloseRequest(t -> {
                Platform.exit();
                System.exit(0);
            });
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
