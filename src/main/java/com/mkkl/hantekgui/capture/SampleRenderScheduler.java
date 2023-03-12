package com.mkkl.hantekgui.capture;

import com.mkkl.hantekgui.OscilloscopeSettings;
import javafx.animation.AnimationTimer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SampleRenderScheduler {
    SamplesBatch samplesBatch;
    boolean shouldUpdate = false;

    long updateTime; //TODO on fps limit change, update this value
    AnimationTimer timer;
    public SampleRenderScheduler(Consumer<SamplesBatch> samplesConsumer, Supplier<CompletableFuture<SamplesBatch>> samplesSupplier) {
        updateTime = (long) ((1/(double) OscilloscopeSettings.getChartFpsLimit())*1e9);

        final long[] nextUpdate = {0};
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(shouldUpdate && now > nextUpdate[0]) {
                    samplesConsumer.accept(samplesBatch);
                    shouldUpdate = false;
                    nextUpdate[0] = now + updateTime;
                    //Request next data frame
                    samplesSupplier.get().thenAccept(samples -> {
                        samplesBatch = samples;
                        shouldUpdate = true;
                    });
                }
            }
        };
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
}
