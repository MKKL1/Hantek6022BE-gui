package com.mkkl.hantekgui.capture;

import com.mkkl.hantekapi.communication.adcdata.AdcInputStream;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;

public class DataProcessor implements Runnable {
    private final ByteBufferInputStream byteBufferInputStream;
    private AdcInputStream adcInputStream;
    private int sampleBatchSize = 4096;

    public DataProcessor(OscilloscopeCommunication oscilloscopeCommunication) {
        this.byteBufferInputStream = new ByteBufferInputStream();
        adcInputStream = oscilloscopeCommunication.getAdcInputStream(this.byteBufferInputStream);
    }

    public synchronized void receiveData(ByteBuffer buffer) {
        byteBufferInputStream.setBuf(buffer);
        notifyAll();
    }

    @Override
    public void run() {
        try {
            if(byteBufferInputStream.buf == null)
                synchronized (this) {
                    wait();
                }
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
                        try {
                            synchronized (this){
                                wait();
                            }
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                //Fire sample batch processed event
                DataReaderManager.fireDataReceivedEvent(new SamplesBatch(ch1data, ch2data));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            try {
                adcInputStream.close();
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
