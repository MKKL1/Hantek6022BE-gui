package com.mkkl.hantekgui.ui.chart.render;

import com.mkkl.hantekgui.capture.SampleBatch;
import com.mkkl.hantekgui.capture.SampleCapture;
import com.mkkl.hantekgui.settings.SettingsRegistry;

import java.util.concurrent.CompletableFuture;

public class SamplesFromCapture implements SampleDataSource {
    private int size;
    private final SampleCapture samplesCapture;

    public SamplesFromCapture(SampleCapture samplesCapture) {
        this.samplesCapture = samplesCapture;
        this.size = SettingsRegistry.sampleCountPerFrame.getValue();
    }

    public void updateSize() {
        this.size = SettingsRegistry.sampleCountPerFrame.getValue();
    }

    @Override
    public CompletableFuture<SampleBatch> requestData() {
        return samplesCapture.requestSamples(size);
    }
}
