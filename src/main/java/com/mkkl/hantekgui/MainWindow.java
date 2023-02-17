package com.mkkl.hantekgui;

import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow {
    @FXML
    public ErrorDataSetRenderer errorDataSetRenderer;
    @FXML
    private Label welcomeText;
    @FXML
    private ScopeChart scopeChart;

    private OscilloscopeCommunication scopeCommunication;

    public void init(OscilloscopeCommunication scopeCommunication) throws IOException {
        this.scopeCommunication = scopeCommunication;
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void initialize() {

    }
}