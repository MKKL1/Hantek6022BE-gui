package com.mkkl.hantekgui;

import com.mkkl.hantekgui.capture.*;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ChartManager implements AutoCloseable{
    private final ScopeChart scopeChart;
    private SamplesCapture samplesCapture = new ContinuousSampleCapture();
    private CaptureMethods captureMethod = CaptureMethods.CONTINUOUS;
    private final SampleRenderScheduler sampleRenderScheduler;
    private final ScopeSamplesRenderer scopeSamplesRenderer;
    private final DataProcessor dataProcessor;
    private final OscilloscopeDataReader oscilloscopeDataReader;

    private OscilloscopeSettings oscilloscopeSettings;

    private static ChartManager instance;

    private ChartManager(OscilloscopeCommunication scopeCommunication, ScopeChart scopeChart) {
        this.oscilloscopeSettings = OscilloscopeSettings.getInstance();
        this.scopeChart = scopeChart;
        this.dataProcessor = new DataProcessor();
        this.oscilloscopeDataReader = new OscilloscopeDataReader(scopeCommunication);
        try {
            dataProcessor.connect(oscilloscopeDataReader.getPipedOutputStream(), scopeCommunication);
        } catch (IOException e) {
            throw new UncheckedIOException(e);//TODO not sure if this is good solution
        }

        new Thread(dataProcessor).start();//TODO TEMPORARY SOLUTION
        new Thread(oscilloscopeDataReader).start();

        this.scopeSamplesRenderer = new ScopeSamplesRenderer(scopeChart);
        this.scopeSamplesRenderer.setXPointsDistance(oscilloscopeSettings.getSampleCountPerFrame(),
                oscilloscopeSettings.getCurrentSampleRate().getTimeBetweenPoints());

        this.sampleRenderScheduler = new SampleRenderScheduler(scopeSamplesRenderer::renderSampleBatch,
                () -> samplesCapture.requestSamples(oscilloscopeSettings.getSampleCountPerFrame()));
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

    }
}
