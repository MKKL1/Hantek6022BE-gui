package com.mkkl.hantekgui.capture;

import com.mkkl.hantekapi.Oscilloscope;
import com.mkkl.hantekapi.communication.adcdata.AsyncScopeDataReader;
import com.mkkl.hantekapi.communication.adcdata.BufferedCallback;
import com.mkkl.hantekapi.communication.adcdata.ByteArrayCallback;
import com.mkkl.hantekgui.protocol.DataReaderListener;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;

import javax.usb.UsbException;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

//Class used in separate thread to minimize gaps between readings
public class OscilloscopeDataReader extends Thread {
    private final OscilloscopeCommunication scopeCommunication;
    private final DataProcessor dataProcessor;
    private final BufferManager bufferManager;
    private final AsyncScopeDataReader asyncScopeDataReader;
    private final short maxPacketSize;
    private final AtomicInteger packetsInQueue = new AtomicInteger(0);

    public OscilloscopeDataReader(OscilloscopeCommunication scopeCommunication, DataProcessor dataProcessor) {
        super("Data Reader Thread");
        this.scopeCommunication = scopeCommunication;
        this.dataProcessor = dataProcessor;

        maxPacketSize = (short) (scopeCommunication.getPacketSize()*4);
        bufferManager = new BufferManager(20, maxPacketSize);
        asyncScopeDataReader = scopeCommunication.getAsyncReader();
        asyncScopeDataReader.registerListener(new BufferedCallback() {
            @Override
            public void onDataReceived(ByteBuffer byteBuffer) {
                packetReceived();
                dataProcessor.receiveData(byteBuffer);
                packetsInQueue.decrementAndGet();
            }
        });
        System.out.println("max packet size " + maxPacketSize);
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
                asyncScopeDataReader.read(maxPacketSize, bufferManager.getNext());
                packetsInQueue.incrementAndGet();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}