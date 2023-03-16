package com.mkkl.hantekgui;

import com.mkkl.hantekgui.protocol.OscilloscopeSampleRate;

public class OscilloscopeSettings {
    private static OscilloscopeSettings instance;
    private OscilloscopeSampleRate currentSampleRate;
    private float chartFpsLimit = 10;
    private int sampleCountPerFrame = 30000;

    public int getSampleCountPerFrame() {
        return sampleCountPerFrame;
    }

    public void setSampleCountPerFrame(int sampleCountPerFrame) {
        this.sampleCountPerFrame = sampleCountPerFrame;
    }

    public float getChartFpsLimit() {
        return chartFpsLimit;
    }

    public void setChartFpsLimit(float chartFpsLimit) {
        this.chartFpsLimit = chartFpsLimit;
    }

    public OscilloscopeSampleRate getCurrentSampleRate() {
        return currentSampleRate;
    }

    public void setCurrentSampleRate(OscilloscopeSampleRate currentSampleRate) {
        this.currentSampleRate = currentSampleRate;
    }

    public static OscilloscopeSettings getInstance() {
        if(instance == null) instance = new OscilloscopeSettings();
        return instance;
    }
}
