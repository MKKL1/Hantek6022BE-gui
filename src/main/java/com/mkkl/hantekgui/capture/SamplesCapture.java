package com.mkkl.hantekgui.capture;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

//TODO name
public interface SamplesCapture extends AutoCloseable {
    CompletableFuture<SamplesBatch> requestSamples(int size);
}
