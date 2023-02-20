package com.mkkl.hantekgui;

import com.mkkl.hantekgui.protocol.DataReaderListener;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class OscilloscopeDataReader implements Runnable{
    private final OscilloscopeCommunication scopeCommunication;
    private final Collection<DataReaderListener> listeners = new HashSet<>();
    private final PipedOutputStream pipedOutputStream = new PipedOutputStream();
    private final int maxPacketSize = 8192;

    public OscilloscopeDataReader(OscilloscopeCommunication scopeCommunication) {
        this.scopeCommunication = scopeCommunication;
//        maxPacketSize = scopeCommunication.getConnectedInterface().getUsbEndpoint(0).getUsbEndpointDescriptor().wMaxPacketSize();
    }

    public void pause() throws InterruptedException {
        wait();
    }

    public void resume() {
        notify();
    }

    private boolean capture = true;
    public void stop() {
        capture = false;
        scopeCommunication.stopCapture();
    }

    @Override
    public void run() {
        scopeCommunication.startCapture();
        while(capture) {
            CompletableFuture<Void> completableFuture = scopeCommunication.asyncRead((short) maxPacketSize,
                    bytes -> {
                        listeners.forEach(x -> x.onDataPackedReceived(bytes));
                        try {
                            pipedOutputStream.write(bytes);
                        } catch (IOException e) {
                            //TODO handle exception
                            e.printStackTrace();
                        }
                    });
            completableFuture.thenAccept(v -> listeners.forEach(DataReaderListener::onDataCompleted));
            completableFuture.exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
            completableFuture.join();
        }
    }

    public void addEventListener(DataReaderListener dataReaderEvent) {
        listeners.add(dataReaderEvent);
    }

    public PipedOutputStream getPipedOutputStream() {
        return pipedOutputStream;
    }
}
