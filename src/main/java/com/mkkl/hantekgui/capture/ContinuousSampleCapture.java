package com.mkkl.hantekgui.capture;

import com.mkkl.hantekgui.AppConstants;
import com.mkkl.hantekgui.settings.SettingsRegistry;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class ContinuousSampleCapture implements SamplesCapture {
    private final BlockingQueue<SampleRequest> requestQueue = new LinkedBlockingQueue<>();
    private final CaptureHistory captureHistory = CaptureHistory.getInstance();
    private final Thread thread;
    public ContinuousSampleCapture() {
        thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    SampleRequest sampleRequest = requestQueue.take();
                    int batchesToRead = (int) Math.ceil((float)sampleRequest.countToRead/AppConstants.sampleBatchSize);
                    SamplesBatch[] samples = captureHistory.getNewSamples(batchesToRead);

                    SamplesBatch samplesBatch = samples[0];
                    int toread = sampleRequest.countToRead-samplesBatch.length;
                    for(int i = 1; i < samples.length && toread > 0; i++) {
                        SamplesBatch cbatch = samples[i];
                        if(toread > cbatch.length) samplesBatch.concatenate(cbatch);
                        else samplesBatch.concatenate(cbatch, toread);
                        toread -= cbatch.length;
                    }
                    if(samplesBatch.length != sampleRequest.countToRead) {
                        System.out.println("thats a big problem");
                        continue;
                    }

                    sampleRequest.getCompletableFuture().complete(samplesBatch);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.start();
        SettingsRegistry.sampleCountPerFrame.addValueChangeListener((oldValue, newValue) -> {
            requestQueue.clear();
            captureHistory.clear();
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread.start();
        });
    }

    @Override
    public CompletableFuture<SamplesBatch> requestSamples(int size) {
        CompletableFuture<SamplesBatch> completableFuture = new CompletableFuture<>();
        requestQueue.add(new SampleRequest(completableFuture, size));
        return completableFuture;
    }

    @Override
    public void close() throws Exception {
        thread.interrupt();
        thread.join();
    }
}

class SampleRequest {
    private final CompletableFuture<SamplesBatch> completableFuture;
    public int countToRead;

    public SampleRequest(CompletableFuture<SamplesBatch> completableFuture, int countToRead) {
        this.completableFuture = completableFuture;
        this.countToRead = countToRead;
    }

    public CompletableFuture<SamplesBatch> getCompletableFuture() {
        return completableFuture;
    }
}