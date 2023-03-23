package com.mkkl.hantekgui.ui.chart.render;

import com.mkkl.hantekgui.capture.SamplesBatch;
import com.mkkl.hantekgui.ui.chart.ScopeChart;

public class SampleRenderer {
    private final ScopeChart scopeChart;
    private float[] xvalues;

    public SampleRenderer(ScopeChart scopeChart) {
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
        scopeChart.getDataSetByChannelId(1).set(xvalues, samplesBatch.getCh2Data());
    }
}
