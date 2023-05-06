package com.mkkl.hantekgui;

import com.mkkl.hantekgui.protocol.*;
import com.mkkl.hantekgui.ui.chart.ChartManager;
import com.mkkl.hantekgui.ui.chart.ScopeChart;
import com.mkkl.hantekgui.settings.SettingsRegistry;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainWindow {
    @FXML
    public ComboBox<OscilloscopeSampleRate> sampleratebox;
    @FXML
    public ComboBox<OscilloscopeVoltRanges> voltagerangebox;
    @FXML
    private ScopeChart scopeChart;

    private List<OscilloscopeChannel> oscilloscopeChannels;

    private AbstractProtocol scopeCommunication;

    public void init(AbstractProtocol scopeCommunication) throws IOException {
        this.scopeCommunication = scopeCommunication;
        OscilloscopeSampleRate[] sampleRates = scopeCommunication.getAvailableSampleRates();
        OscilloscopeSampleRate sampleRate = Arrays.stream(sampleRates).filter(x -> x.samplesPerSecond() == 100000).findFirst().orElseThrow();

        //TODO move this part to more noticeable place
        SettingsRegistry.currentSampleRate.setValue(sampleRate);
        SettingsRegistry.currentSampleRate.addValueChangeListener((oldValue, newValue) -> scopeCommunication.setSampleRate(newValue));


        scopeChart.setChannels(scopeCommunication.getChannels());
        oscilloscopeChannels = new ArrayList<>(Arrays.asList(scopeCommunication.getChannels()));
        initializeMenu();
        ChartManager chartManager = ChartManager.create(scopeCommunication, scopeChart);
    }

    private boolean paused = false;
    @FXML
    protected void onHelloButtonClick() {
        //SettingsRegistry.chartFpsLimit.setValue(1f);
        //SettingsRegistry.sampleCountPerFrame.setValue(10000);
        //ChartManager.getInstance().setTimeBase(0.5f);
        if(!paused) {
            ChartManager.getInstance().pause();
            paused = true;
        }
        else {
            ChartManager.getInstance().resume();
            paused = false;
        }
    }

    @FXML
    protected void initialize() {

    }

    private void initializeMenu() {
        Callback<ListView<OscilloscopeSampleRate>, ListCell<OscilloscopeSampleRate>> factory = x -> new ListCell<>() {
            @Override
            protected void updateItem(OscilloscopeSampleRate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "error" : item.toString());
            }
        };
        sampleratebox.setCellFactory(factory);
        sampleratebox.setButtonCell(factory.call(null));

        List<OscilloscopeSampleRate> sampleRateList = new ArrayList<>(Arrays.asList(scopeCommunication.getAvailableSampleRates()));
        sampleratebox.setItems(FXCollections.observableList(sampleRateList));
        sampleratebox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observableValue, oscilloscopeSampleRate, t1) -> {
                    scopeCommunication.stopCapture();
                            SettingsRegistry.currentSampleRate.setValue(observableValue.getValue());
                    scopeCommunication.startCapture();
                        });

        List<OscilloscopeVoltRanges> voltRangesList = new ArrayList<>(Arrays.asList(scopeCommunication.getVoltageRanges()));
        voltagerangebox.setItems(FXCollections.observableList(voltRangesList));
        voltagerangebox.getSelectionModel().selectedItemProperty().addListener(((observableValue, oscilloscopeVoltRanges, t1) -> {
            scopeCommunication.stopCapture();
            scopeCommunication.setVoltageRange(oscilloscopeChannels.get(0), observableValue.getValue());
            scopeCommunication.setVoltageRange(oscilloscopeChannels.get(1), observableValue.getValue());
            scopeCommunication.startCapture();
        }));
    }
}