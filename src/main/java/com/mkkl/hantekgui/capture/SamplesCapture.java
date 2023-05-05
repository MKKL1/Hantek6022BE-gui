package com.mkkl.hantekgui.capture;

import java.util.concurrent.CompletableFuture;

//TODO name
public interface SamplesCapture extends AutoCloseable {
    CompletableFuture<SampleBatch> requestSamples(int size);
}
