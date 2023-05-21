package com.mkkl.hantekgui.ui.chart.render;

import com.mkkl.hantekgui.capture.SampleBatch;
import com.mkkl.hantekgui.protocol.OscilloscopeSampleRate;
import com.mkkl.hantekgui.ui.chart.ScopeChart;

public class SampleRenderer implements RenderSchedulerListener {
    private final ScopeChart scopeChart;
    private final SampleRenderScheduler sampleRenderScheduler;
    private float[] xvalues;

    public SampleRenderer(ScopeChart scopeChart, SampleDataSource sampleDataSource) {
        this.scopeChart = scopeChart;
        sampleRenderScheduler = new SampleRenderScheduler(sampleDataSource);
        sampleRenderScheduler.registerListener(this);
        sampleRenderScheduler.start();
    }

    public void pause() {
        sampleRenderScheduler.stop();
    }

    public void resume() {
        sampleRenderScheduler.start();
    }

    public void refresh() {
        sampleRenderScheduler.reset();
    }

    public void updateXAxisPoints(OscilloscopeSampleRate oscilloscopeSampleRate, int points) {
        xvalues = new float[points];
        float baseDist = oscilloscopeSampleRate.getTimeBetweenPoints();
        for(int i = 0; i < points; i++) {
            xvalues[i] = i*baseDist;
        }
    }

    @Override
    public void onRenderTick(SampleBatch batchToRender) {
        for (int i = 0; i < 2; i++) {
            scopeChart.getDataSetByChannelId(i).set(xvalues, batchToRender.getChannelData(i));
        }
    }
}
