package com.mkkl.hantekgui;

import com.mkkl.hantekgui.capture.SamplesBatch;

public class ScopeSamplesRenderer {
    private final ScopeChart scopeChart;
    private float[] xvalues;

    public ScopeSamplesRenderer(ScopeChart scopeChart) {
        this.scopeChart = scopeChart;
    }

    public void setXPointsDistance(int count, float distance) {
        xvalues = new float[count];
        float calcdist = 0;
        for(int i = 0; i < count; i++) {
            xvalues[i] = calcdist;
            calcdist += distance;
        }
    }

    public void renderSampleBatch(SamplesBatch samplesBatch) {
        scopeChart.getDataSetByChannelId(0).set(xvalues, samplesBatch.getCh1Data());
        scopeChart.getDataSetByChannelId(0).set(xvalues, samplesBatch.getCh2Data());
    }
}
