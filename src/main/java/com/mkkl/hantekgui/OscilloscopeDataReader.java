package com.mkkl.hantekgui;

import com.mkkl.hantekgui.protocol.DataReaderListener;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class OscilloscopeDataReader implements Runnable{
    private final OscilloscopeCommunication scopeCommunication;
    private final Collection<DataReaderListener> listeners = new HashSet<>();

    public OscilloscopeDataReader(OscilloscopeCommunication scopeCommunication) {
        this.scopeCommunication = scopeCommunication;
    }

    public void pause() throws InterruptedException {
        wait();
    }

    //TODO name
    public void unpause() {
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
            CompletableFuture<Void> completableFuture = scopeCommunication.asyncRead((short) 8192,
                    bytes -> listeners.forEach(x -> x.onDataPackedReceived(bytes)));
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
}
