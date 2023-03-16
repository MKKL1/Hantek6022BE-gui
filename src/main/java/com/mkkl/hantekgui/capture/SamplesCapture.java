package com.mkkl.hantekgui.capture;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

//TODO name
public interface SamplesCapture extends Closeable {
    CompletableFuture<SamplesBatch> requestSamples(int size);
}
