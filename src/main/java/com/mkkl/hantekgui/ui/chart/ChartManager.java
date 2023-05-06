package com.mkkl.hantekgui.ui.chart;

import com.mkkl.hantekgui.protocol.OscilloscopeSampleRate;
import com.mkkl.hantekgui.ui.chart.render.SamplesFromCapture;
import com.mkkl.hantekgui.ui.chart.render.SampleRenderer;
import com.mkkl.hantekgui.settings.SettingsRegistry;
import com.mkkl.hantekgui.capture.*;
import com.mkkl.hantekgui.protocol.AbstractProtocol;

public class ChartManager implements AutoCloseable{
    private final ScopeChart scopeChart;
    private SampleCapture samplesCapture = new ContinuousSampleCapture();
    private CaptureMethods captureMethod = CaptureMethods.CONTINUOUS;
    private final SampleRenderer sampleRenderer;
    private final SamplesFromCapture samplesFromCapture;

    private final DataProcessor dataProcessor;
    private final DataReaderProcess dataReaderProcess;
    private final AbstractProtocol scopeCommunication;

    private static ChartManager instance;

    private ChartManager(AbstractProtocol scopeCommunication, ScopeChart scopeChart) {
        this.scopeCommunication = scopeCommunication;
        this.scopeChart = scopeChart;

        this.dataProcessor = new DataProcessor(scopeCommunication);
        this.dataReaderProcess = new DataReaderProcess(scopeCommunication, dataProcessor);
        dataProcessor.start();
        dataReaderProcess.start();

        this.samplesFromCapture = new SamplesFromCapture(samplesCapture);
        this.sampleRenderer = new SampleRenderer(scopeChart, samplesFromCapture);

        registerSettingListeners();
        refreshChart();
    }

    private void registerSettingListeners() {
        SettingsRegistry.sampleCountPerFrame.addValueChangeListener((oldValue, newValue) -> {
            refreshChart();
        });

        SettingsRegistry.currentSampleRate.addValueChangeListener((oldValue, newValue) -> {
            System.out.println("New sample rate " + newValue.toString());
            refreshChart();
        });
    }

    public void refreshChart() {
        this.samplesFromCapture.updateSize();
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

    public CaptureMethods getCaptureMethod() {
        return captureMethod;
    }

    public void setCaptureMethod(CaptureMethods captureMethod) {
        try {
            samplesCapture.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (captureMethod) {
            case CONTINUOUS -> samplesCapture = new ContinuousSampleCapture();
            case SINGLE -> System.out.println("not implemented");//TODO implement
        }
        this.captureMethod = captureMethod;
    }

    public static ChartManager create(AbstractProtocol scopeCommunication, ScopeChart scopeChart) {
        if(instance == null) instance = new ChartManager(scopeCommunication, scopeChart);
        return instance;
    }

    public static ChartManager getInstance() {
        return instance;
    }

    @Override
    public void close() throws Exception {
        dataReaderProcess.interrupt();
        dataReaderProcess.join();

        dataProcessor.interrupt();
        dataProcessor.join();

        samplesCapture.close();
    }
}
