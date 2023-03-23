package com.mkkl.hantekgui.capture;

import com.mkkl.hantekapi.Oscilloscope;
import com.mkkl.hantekapi.communication.adcdata.AsyncScopeDataReader;
import com.mkkl.hantekapi.communication.adcdata.ByteArrayCallback;
import com.mkkl.hantekgui.protocol.DataReaderListener;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;

import javax.usb.UsbException;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

//Class used in separate thread to minimize gaps between readings
public class OscilloscopeDataReader extends Thread {
    private final OscilloscopeCommunication scopeCommunication;
    private final PipedOutputStream pipedOutputStream = new PipedOutputStream();
    private final AsyncScopeDataReader asyncScopeDataReader;
    private final short maxPacketSize;
    private final AtomicInteger packetsInQueue = new AtomicInteger(0);

    public OscilloscopeDataReader(OscilloscopeCommunication scopeCommunication) {
        this.scopeCommunication = scopeCommunication;
        maxPacketSize = (short) (scopeCommunication.getPacketSize()*4);
        asyncScopeDataReader = scopeCommunication.getAsyncReader();
        asyncScopeDataReader.registerListener(new ByteArrayCallback() {
            @Override
            public void onDataReceived(byte[] bytes) {
                try {
                    pipedOutputStream.write(bytes);
                    packetReceived();
                    packetsInQueue.decrementAndGet();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                asyncScopeDataReader.read(maxPacketSize);
                packetsInQueue.incrementAndGet();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public PipedOutputStream getPipedOutputStream() {
        return pipedOutputStream;
    }
}