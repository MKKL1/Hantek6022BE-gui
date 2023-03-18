package com.mkkl.hantekgui;

import com.mkkl.hantekgui.protocol.OscilloscopeDevice;
import com.mkkl.hantekgui.ui.chart.ChartManager;
import com.mkkl.hantekgui.ui.chart.ScopeChart;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;
import com.mkkl.hantekgui.protocol.OscilloscopeSampleRate;
import com.mkkl.hantekgui.settings.SettingsRegistry;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainWindow {
    @FXML
    public ComboBox<OscilloscopeSampleRate> sampleratebox;
    @FXML
    private ScopeChart scopeChart;

    private OscilloscopeCommunication scopeCommunication;

    public void init(OscilloscopeCommunication scopeCommunication) throws IOException {
        this.scopeCommunication = scopeCommunication;
        OscilloscopeSampleRate sampleRate = scopeCommunication.getAvailableSampleRates().stream().filter(x -> x.samplesPerSecond() == 100000).findFirst().orElseThrow();

        //TODO move this part to more noticeable place
        SettingsRegistry.currentSampleRate.setValue(sampleRate);
        SettingsRegistry.currentSampleRate.addValueChangeListener((oldValue, newValue) -> scopeCommunication.setSampleRate(newValue));


        scopeChart.setChannels(scopeCommunication.getChannels().stream().toList());
        initializeMenu();
        ChartManager chartManager = ChartManager.create(scopeCommunication, scopeChart);
    }

    @FXML
    protected void onHelloButtonClick() {
        //SettingsRegistry.chartFpsLimit.setValue(1f);
        //SettingsRegistry.sampleCountPerFrame.setValue(10000);
        ChartManager.getInstance().setTimeBase(0.5f);
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

        List<OscilloscopeSampleRate> sampleRateList = new ArrayList<>(scopeCommunication.getAvailableSampleRates());
        sampleratebox.setItems(FXCollections.observableList(sampleRateList));
        sampleratebox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observableValue, oscilloscopeSampleRate, t1) -> {
                    scopeCommunication.stopCapture();
                            SettingsRegistry.currentSampleRate.setValue(observableValue.getValue());
                    scopeCommunication.startCapture();
                        });
    }
}