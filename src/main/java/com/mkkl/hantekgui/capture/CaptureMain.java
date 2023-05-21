package com.mkkl.hantekgui.capture;

import com.mkkl.hantekgui.protocol.AbstractProtocol;
import com.mkkl.hantekgui.ui.chart.render.SampleDataSource;
import com.mkkl.hantekgui.ui.chart.render.SampleRenderer;
import com.mkkl.hantekgui.ui.chart.render.SamplesFromCapture;

//TODO name
public class CaptureMain implements AutoCloseable{
    private final CaptureEventDispatcher captureEventDispatcher;
    private final CaptureHistory captureHistory;
    private SampleCapture samplesCapture;
    private CaptureMethods captureMethod = CaptureMethods.CONTINUOUS;
    private final SamplesFromCapture samplesFromCapture;

    private final DataProcessor dataProcessor;
    private final DataReaderProcess dataReaderProcess;

    public CaptureMain(AbstractProtocol protocol) {
        captureEventDispatcher = new CaptureEventDispatcher();
        captureHistory = new CaptureHistory(64);

        dataProcessor = new DataProcessor(protocol, captureHistory, captureEventDispatcher);
        dataReaderProcess = new DataReaderProcess(protocol, dataProcessor);

        samplesCapture = new ContinuousSampleCapture(captureHistory);
        samplesFromCapture = new SamplesFromCapture(samplesCapture);


        dataProcessor.start();
        dataReaderProcess.start();
    }

    public CaptureEventDispatcher getEventDispatcher() {
        return captureEventDispatcher;
    }

    //TODO implement
//    public void setCaptureMethod(CaptureMethods captureMethod) {
//        close capture
//        this.captureMethod = captureMethod;
//    }

    public SampleDataSource getSampleDataSource() {
        return samplesFromCapture;
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
