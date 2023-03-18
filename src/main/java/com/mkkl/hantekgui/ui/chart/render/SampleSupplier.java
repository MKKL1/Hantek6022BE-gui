package com.mkkl.hantekgui.ui.chart.render;

import com.mkkl.hantekgui.capture.SamplesBatch;
import com.mkkl.hantekgui.capture.SamplesCapture;
import com.mkkl.hantekgui.settings.SettingsRegistry;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class SampleSupplier implements Supplier<CompletableFuture<SamplesBatch>> {
    private Integer size;//using Integer class instead of primitive type to update value by pointer
    private final SamplesCapture samplesCapture;

    public SampleSupplier(SamplesCapture samplesCapture) {
        this.samplesCapture = samplesCapture;
        this.size = SettingsRegistry.sampleCountPerFrame.getValue();
    }

    public void updateSize() {
        this.size = SettingsRegistry.sampleCountPerFrame.getValue();
    }

    @Override
    public CompletableFuture<SamplesBatch> get() {
        return samplesCapture.requestSamples(size);
    }
}
