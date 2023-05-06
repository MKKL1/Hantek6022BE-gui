package com.mkkl.hantekgui.capture;

import java.util.concurrent.CompletableFuture;

//TODO name
public interface SampleCapture extends AutoCloseable {
    CompletableFuture<SampleBatch> requestSamples(int size);
}
