package com.mkkl.hantekgui.ui.chart.render;

import com.mkkl.hantekgui.capture.SamplesBatch;
import com.mkkl.hantekgui.settings.SettingsRegistry;
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
        calculateUpdateTime(SettingsRegistry.chartFpsLimit.getValue());
        SettingsRegistry.chartFpsLimit.addValueChangeListener((oldValue, newValue) -> calculateUpdateTime(newValue));

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

    public SampleRenderScheduler(SampleRenderer sampleRenderer, Supplier<CompletableFuture<SamplesBatch>> samplesSupplier) {
        this(sampleRenderer::renderSampleBatch, samplesSupplier);
    }

    private void calculateUpdateTime(float fpsLimit) {
        updateTime = (long) ((1/fpsLimit)*1e9);
    }

    public void reset() {
        samplesSupplier.get().thenAccept(samples -> {
            samplesBatch = samples;
            shouldUpdate = true;
        });
    }

    public void start() {
        //Requesting first data frame
        reset();
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
}
