package com.mkkl.hantekgui.capture;

import com.mkkl.hantekapi.communication.adcdata.AdcInputStream;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class DataProcessor implements Runnable {
    private PipedInputStream pipedInputStream;
    private AdcInputStream adcInputStream;
    private int sampleBatchSize = 4096;
    public DataProcessor() {
        pipedInputStream = new PipedInputStream();
    }

    public DataProcessor(PipedOutputStream pipedOutputStream, OscilloscopeCommunication oscilloscopeCommunication) throws IOException {
        this();
        connect(pipedOutputStream, oscilloscopeCommunication);
    }

    public void connect(PipedOutputStream pipedOutputStream, OscilloscopeCommunication oscilloscopeCommunication) throws IOException {
        this.pipedInputStream.connect(pipedOutputStream);
        adcInputStream = oscilloscopeCommunication.getAdcInputStream(pipedInputStream);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                int readSamples = 0;
                float[] ch1data = new float[sampleBatchSize];
                float[] ch2data = new float[sampleBatchSize];
                while(readSamples < sampleBatchSize) {
                    try {
                        float[] sample = adcInputStream.readFormattedVoltages();
                        ch1data[readSamples] = sample[0];
                        ch2data[readSamples] = sample[1];
                        readSamples++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //Fire sample batch processed event
                DataReaderManager.fireDataReceivedEvent(new SamplesBatch(ch1data, ch2data));
            }
        } finally {
            try {
                adcInputStream.close();
                pipedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getSampleBatchSize() {
        return sampleBatchSize;
    }

    public void setSampleBatchSize(int sampleBatchSize) {
        this.sampleBatchSize = sampleBatchSize;
    }
}
