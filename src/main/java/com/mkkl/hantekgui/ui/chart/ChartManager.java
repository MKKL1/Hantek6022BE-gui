package com.mkkl.hantekgui.ui.chart;

import com.mkkl.hantekgui.protocol.OscilloscopeSampleRate;
import com.mkkl.hantekgui.ui.chart.render.SampleRenderScheduler;
import com.mkkl.hantekgui.ui.chart.render.SampleSupplier;
import com.mkkl.hantekgui.ui.chart.render.SampleRenderer;
import com.mkkl.hantekgui.settings.SettingsRegistry;
import com.mkkl.hantekgui.capture.*;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ChartManager implements AutoCloseable{
    private final ScopeChart scopeChart;
    private SamplesCapture samplesCapture = new ContinuousSampleCapture();
    private CaptureMethods captureMethod = CaptureMethods.CONTINUOUS;
    private final SampleRenderScheduler sampleRenderScheduler;
    private final SampleRenderer sampleRenderer;
    private final DataProcessor dataProcessor;
    private final OscilloscopeDataReader oscilloscopeDataReader;
    private final SampleSupplier sampleSupplier;
    private final OscilloscopeCommunication scopeCommunication;

    private static ChartManager instance;

    private ChartManager(OscilloscopeCommunication scopeCommunication, ScopeChart scopeChart) {
        this.scopeCommunication = scopeCommunication;
        this.scopeChart = scopeChart;
        this.dataProcessor = new DataProcessor(scopeCommunication);
        this.oscilloscopeDataReader = new OscilloscopeDataReader(scopeCommunication, dataProcessor);

        new Thread(dataProcessor).start();//TODO TEMPORARY SOLUTION
        oscilloscopeDataReader.start();

        this.sampleRenderer = new SampleRenderer(scopeChart);
        this.sampleSupplier = new SampleSupplier(samplesCapture);
        this.sampleRenderScheduler = new SampleRenderScheduler(sampleRenderer, sampleSupplier);
        this.sampleRenderScheduler.start();

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
        this.sampleSupplier.updateSize();
        this.sampleRenderer.setXPointsDistance(
                SettingsRegistry.sampleCountPerFrame.getValue(),
                SettingsRegistry.currentSampleRate.getValue().getTimeBetweenPoints());
        this.sampleRenderScheduler.reset();
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

    public CaptureMethods getCaptureMethod() {
        return captureMethod;
    }

    public void setCaptureMethod(CaptureMethods captureMethod) {
        try {
            samplesCapture.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (captureMethod) {
            case CONTINUOUS -> samplesCapture = new ContinuousSampleCapture();
            case SINGLE -> System.out.println("not implemented");//TODO implement
        }
        this.captureMethod = captureMethod;
    }

    public static ChartManager create(OscilloscopeCommunication scopeCommunication, ScopeChart scopeChart) {
        if(instance == null) instance = new ChartManager(scopeCommunication, scopeChart);
        return instance;
    }

    public static ChartManager getInstance() {
        return instance;
    }

    @Override
    public void close() throws Exception {
        oscilloscopeDataReader.interrupt();
        oscilloscopeDataReader.join();
    }
}
