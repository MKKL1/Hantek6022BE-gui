package com.mkkl.hantekgui;

import com.mkkl.hantekgui.capture.CaptureMethod;
import com.mkkl.hantekgui.capture.SampleRenderScheduler;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;

public class ChartManager implements AutoCloseable{
    private final ScopeChart scopeChart;
    private CaptureMethod captureMethod;
//    private final OscilloscopeDataReader dataReader;
//    private final Thread dataReaderThread;
//    private final DataProcessor dataProcessor;
//    private final Thread dataProcessorThread;
    private static ChartManager instance;

    private ChartManager(OscilloscopeCommunication scopeCommunication, ScopeChart scopeChart) {
        this.scopeChart = scopeChart;
//        dataReader = new OscilloscopeDataReader(scopeCommunication);
//        dataProcessor = new DataProcessor(scopeCommunication, dataReader);
//        dataProcessorThread = new Thread(dataProcessor, "Scope Data Processor");
//        dataProcessorThread.start();
//
//        dataReaderThread = new Thread(dataReader, "Scope Data Reader");
//        dataReaderThread.start();
    }

    public static ChartManager create(OscilloscopeCommunication scopeCommunication, ScopeChart scopeChart) {
        if(instance == null) instance = new ChartManager(scopeCommunication, scopeChart);
        return instance;
    }

    public void displayRealTimeData() {
        captureMethod = new SampleRenderScheduler();
    }

    public CaptureMethod getCaptureMethod() {
        return captureMethod;
    }

    public static ChartManager getInstance() {
        return instance;
    }

    @Override
    public void close() throws Exception {

    }
}
