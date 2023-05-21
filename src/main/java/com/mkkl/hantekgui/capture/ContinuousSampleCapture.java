package com.mkkl.hantekgui.capture;

import com.mkkl.hantekgui.AppConstants;
import com.mkkl.hantekgui.settings.SettingsRegistry;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class ContinuousSampleCapture implements SampleCapture {
    private final BlockingQueue<SampleRequest> requestQueue = new LinkedBlockingQueue<>();
    private final CaptureHistory captureHistory;
    private final Thread thread;
    public ContinuousSampleCapture(CaptureHistory captureHistory) {
        this.captureHistory = captureHistory;


        thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    SampleRequest sampleRequest = requestQueue.take();
                    int batchesToRead = (int) Math.ceil((float)sampleRequest.countToRead/AppConstants.sampleBatchSize);
                    SampleBatch[] samples = captureHistory.getSamplesOrWait(batchesToRead);
                    //System.out.println(Arrays.toString(samples));
                    SampleBatch sampleBatch = samples[0];
                    int toread = sampleRequest.countToRead - sampleBatch.length;
                    for(int i = 1; i < samples.length && toread > 0; i++) {
                        SampleBatch cbatch = samples[i];
                        if(toread > cbatch.length) sampleBatch.addSamples(cbatch);
                        else sampleBatch.addSamples(cbatch, toread);
                        toread -= cbatch.length;
                    }
                    if(sampleBatch.length != sampleRequest.countToRead) {
                        System.out.println("thats a big problem");
                        continue;
                    }

                    sampleRequest.getCompletableFuture().complete(sampleBatch);
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
    public CompletableFuture<SampleBatch> requestSamples(int size) {
        CompletableFuture<SampleBatch> completableFuture = new CompletableFuture<>();
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
    private final CompletableFuture<SampleBatch> completableFuture;
    public int countToRead;

    public SampleRequest(CompletableFuture<SampleBatch> completableFuture, int countToRead) {
        this.completableFuture = completableFuture;
        this.countToRead = countToRead;
    }

    public CompletableFuture<SampleBatch> getCompletableFuture() {
        return completableFuture;
    }
}