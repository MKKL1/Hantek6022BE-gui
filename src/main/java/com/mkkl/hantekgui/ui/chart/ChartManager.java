package com.mkkl.hantekgui.ui.chart;

import com.mkkl.hantekgui.protocol.OscilloscopeSampleRate;
import com.mkkl.hantekgui.ui.chart.render.SamplesFromCapture;
import com.mkkl.hantekgui.ui.chart.render.SampleRenderer;
import com.mkkl.hantekgui.settings.SettingsRegistry;
import com.mkkl.hantekgui.capture.*;
import com.mkkl.hantekgui.protocol.AbstractProtocol;

public class ChartManager {
    private final ScopeChart scopeChart;

    private final AbstractProtocol scopeCommunication;
    private final SampleRenderer sampleRenderer;
    private static ChartManager instance;

    private ChartManager(AbstractProtocol scopeCommunication, ScopeChart scopeChart) {
        this.scopeCommunication = scopeCommunication;
        this.scopeChart = scopeChart;
        this.sampleRenderer = new SampleRenderer(scopeChart, samplesFromCapture);

        registerSettingListeners();
        refreshChart();
    }

    private void registerSettingListeners() {
        SettingsRegistry.sampleCountPerFrame.addValueChangeListener((oldValue, newValue) -> refreshChart());
        SettingsRegistry.currentSampleRate.addValueChangeListener((oldValue, newValue) -> refreshChart());
    }

    public void refreshChart() {
        this.sampleRenderer.updateXAxisPoints(SettingsRegistry.currentSampleRate.getValue(), SettingsRegistry.sampleCountPerFrame.getValue());
        this.sampleRenderer.refresh();
    }

    public void setTimeBase(float timeBase) {
        int requiredSamplePerSec = (int) (SettingsRegistry.sampleCountPerFrame.getValue()/timeBase);
        //Find best
        for(OscilloscopeSampleRate oscilloscopeSampleRate : scopeCommunication.getAvailableSampleRates()) {
            if (oscilloscopeSampleRate.samplesPerSecond() >= requiredSamplePerSec) {
                scopeCommunication.stopCapture();
                SettingsRegistry.currentSampleRate.setValue(oscilloscopeSampleRate);
                scopeCommunication.startCapture();
                break;
            }
        }
    }

    public void pause() {
        sampleRenderer.pause();
    }

    public void resume() {
        sampleRenderer.resume();
    }

    public static ChartManager create(AbstractProtocol scopeCommunication, ScopeChart scopeChart) {
        if(instance == null) instance = new ChartManager(scopeCommunication, scopeChart);
        return instance;
    }

    public static ChartManager getInstance() {
        return instance;
    }


}
