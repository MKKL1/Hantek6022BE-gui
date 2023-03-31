package com.mkkl.hantekgui.capture;

import com.mkkl.hantekapi.communication.adcdata.ADCDataFormatter;
import com.mkkl.hantekgui.AppConstants;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;

import java.io.EOFException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DataProcessor implements Runnable {
    private final BlockingQueue<ByteBuffer> bufferQueue = new LinkedBlockingQueue<>();
    private final ADCDataFormatter adcDataFormatter;
    private final int sampleBatchSize;

    public DataProcessor(OscilloscopeCommunication oscilloscopeCommunication) {
        this.sampleBatchSize = AppConstants.packetSize/2;
        this.adcDataFormatter = oscilloscopeCommunication.getAdcDataFormatter();
    }

    public void receiveData(ByteBuffer buffer) {
        bufferQueue.add(buffer);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ByteBuffer byteBuffer = bufferQueue.take();
                int samplesRead = 0;
                float[] ch1data = new float[sampleBatchSize];
                float[] ch2data = new float[sampleBatchSize];
                while(byteBuffer.remaining()>=2) {
                    float[] samplefloat = adcDataFormatter.formatSample(byteBuffer.get(), byteBuffer.get());
                    ch1data[samplesRead] = samplefloat[0];
                    ch2data[samplesRead] = samplefloat[1];
                    samplesRead++;
                }
                DataReaderManager.fireDataReceivedEvent(new SamplesBatch(ch1data, ch2data));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {

        }
    }
}
