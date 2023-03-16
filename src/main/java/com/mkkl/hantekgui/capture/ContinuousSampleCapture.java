package com.mkkl.hantekgui.capture;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ContinuousSampleCapture implements SamplesCapture{
    private SamplesBatch currentSample;
    private int countToRead;
    CompletableFuture<SamplesBatch> completableFuture;
    private DataReceivedEvent dataReceivedEvent;

    public ContinuousSampleCapture() {
        dataReceivedEvent = samplesBatch -> {
            //Adding multiple batches until required batch size is met
            if(countToRead == 0) return;
            //This solution doesn't take into account situation where first batch could be larger than required size
            //TODO fix
            countToRead -= samplesBatch.length;
            if(currentSample == null) currentSample = samplesBatch;
            else {
                if(countToRead >= 0) currentSample.concatenate(samplesBatch);
                else currentSample.concatenate(samplesBatch, samplesBatch.length+countToRead);
            }
            if(countToRead <= 0) {
                completableFuture.complete(new SamplesBatch(currentSample.getCh1Data(), currentSample.getCh2Data()));
                currentSample = null;
                countToRead = 0;
            }
        };
        DataReaderManager.register(dataReceivedEvent);
    }

    //TODO request queue
    @Override
    public CompletableFuture<SamplesBatch> requestSamples(int size) {
        completableFuture = new CompletableFuture<>();
        countToRead = size;
        return completableFuture;
    }

    @Override
    public void close() throws IOException {
        DataReaderManager.unregister(dataReceivedEvent);
    }
}
