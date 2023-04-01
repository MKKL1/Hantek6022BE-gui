package com.mkkl.hantekgui.capture;

import com.mkkl.hantekapi.communication.readers.BufferedCallback;
import com.mkkl.hantekapi.communication.readers.async.ReuseTransferAsyncReader;
import com.mkkl.hantekgui.AppConstants;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

//Class used in separate thread to minimize gaps between readings
public class OscilloscopeDataReader extends Thread {
    private final OscilloscopeCommunication scopeCommunication;
    private final DataProcessor dataProcessor;
    private final ReuseTransferAsyncReader asyncScopeDataReader;
    private final short packetSize;
    private final AtomicInteger packetsInQueue = new AtomicInteger(0);

    public OscilloscopeDataReader(OscilloscopeCommunication scopeCommunication, DataProcessor dataProcessor) {
        super("Data Reader Thread");
        this.scopeCommunication = scopeCommunication;
        this.dataProcessor = dataProcessor;

        //TODO calculate by multiple of max packet size of endpoint
        packetSize = AppConstants.packetSize;
        asyncScopeDataReader = scopeCommunication.getReuseAsyncReader(packetSize, 20);
        asyncScopeDataReader.registerListener(new BufferedCallback(false) {
            @Override
            public void onDataReceived(ByteBuffer byteBuffer) {
                packetReceived();
                dataProcessor.receiveData(byteBuffer);
                packetsInQueue.decrementAndGet();
            }
        });
    }

    private volatile boolean capture = true;

    private void packetReceived() {
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public void run() {
        scopeCommunication.startCapture();
        while(capture) {
            try {
                while(packetsInQueue.get() > 10)
                    synchronized (this) {
                        wait();
                    }
                asyncScopeDataReader.read();
                packetsInQueue.incrementAndGet();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}