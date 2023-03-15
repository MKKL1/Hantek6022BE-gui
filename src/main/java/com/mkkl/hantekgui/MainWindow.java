package com.mkkl.hantekgui;

import com.mkkl.hantekgui.protocol.DataReaderListener;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;
import com.mkkl.hantekgui.protocol.OscilloscopeSampleRate;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
        OscilloscopeSettings oscilloscopeSettings = OscilloscopeSettings.getInstance();
        OscilloscopeSampleRate sampleRate =  scopeCommunication.getAvailableSampleRates().stream().filter(x -> x.samplesPerSecond() == 100000).findFirst().orElseThrow();
        oscilloscopeSettings.setCurrentSampleRate(sampleRate);
        scopeCommunication.setSampleRate(sampleRate);
        ChartManager chartManager = ChartManager.create(scopeCommunication, scopeChart);
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void initialize() {

    }
}