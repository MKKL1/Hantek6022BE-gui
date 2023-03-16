package com.mkkl.hantekgui.capture;

import com.mkkl.hantekgui.OscilloscopeSettings;
import javafx.animation.AnimationTimer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SampleRenderScheduler {
    SamplesBatch samplesBatch;
    boolean shouldUpdate = false;
    private final Supplier<CompletableFuture<SamplesBatch>> samplesSupplier;

    long updateTime; //TODO on fps limit change, update this value
    AnimationTimer timer;
    public SampleRenderScheduler(Consumer<SamplesBatch> renderer, Supplier<CompletableFuture<SamplesBatch>> samplesSupplier) {
        this.samplesSupplier = samplesSupplier;
        updateTime = (long) ((1/(double) OscilloscopeSettings.getInstance().getChartFpsLimit())*1e9);
        final long[] nextUpdate = {0};
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(shouldUpdate && now > nextUpdate[0]) {
                    renderer.accept(samplesBatch);
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
        //Requesting first data frame
        samplesSupplier.get().thenAccept(samples -> {
            samplesBatch = samples;
            shouldUpdate = true;
        });
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
}
