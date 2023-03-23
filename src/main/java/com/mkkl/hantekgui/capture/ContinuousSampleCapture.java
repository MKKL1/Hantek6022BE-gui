package com.mkkl.hantekgui.capture;

import com.mkkl.hantekgui.settings.SettingsRegistry;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class ContinuousSampleCapture implements SamplesCapture{
    CompletableFuture<SamplesBatch> completableFuture;
    private final BlockingQueue<SampleRequest> requestQueue = new LinkedBlockingQueue<>();
    private final DataReceivedEvent dataReceivedEvent;

    private SampleRequest currentRequest;

    public ContinuousSampleCapture() {
        dataReceivedEvent = samplesBatch -> {
            if(currentRequest == null && !requestQueue.isEmpty()) {
                try {
                    currentRequest = requestQueue.take();
                } catch (InterruptedException e) {
                    //TODO handle
                    e.printStackTrace();
                }
            }

            if(currentRequest != null && currentRequest.countToRead > 0) {
                currentRequest.countToRead -= samplesBatch.length;
                if(currentRequest.samplesBatch == null) currentRequest.samplesBatch = samplesBatch;
                else {
                    if(currentRequest.countToRead >= 0) currentRequest.samplesBatch.concatenate(samplesBatch);
                    else currentRequest.samplesBatch.concatenate(samplesBatch, samplesBatch.length+currentRequest.countToRead);
                }
                if(currentRequest.countToRead <= 0) {
                    currentRequest.getCompletableFuture().complete(currentRequest.samplesBatch);
                    currentRequest = null;
                }
            }
        };
        DataReaderManager.register(dataReceivedEvent);
        SettingsRegistry.sampleCountPerFrame.addValueChangeListener((oldValue, newValue) -> {
            currentRequest = null;
            requestQueue.clear();
        });
    }

    @Override
    public CompletableFuture<SamplesBatch> requestSamples(int size) {
        completableFuture = new CompletableFuture<>();
        requestQueue.add(new SampleRequest(completableFuture, size));
        return completableFuture;
    }

    @Override
    public void close() throws IOException {
        DataReaderManager.unregister(dataReceivedEvent);
    }
}

class SampleRequest {
    private final CompletableFuture<SamplesBatch> completableFuture;
    public int countToRead;
    public SamplesBatch samplesBatch;

    public SampleRequest(CompletableFuture<SamplesBatch> completableFuture, int countToRead) {
        this.completableFuture = completableFuture;
        this.countToRead = countToRead;
    }

    public CompletableFuture<SamplesBatch> getCompletableFuture() {
        return completableFuture;
    }
}