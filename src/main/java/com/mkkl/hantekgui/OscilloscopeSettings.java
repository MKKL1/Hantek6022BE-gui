package com.mkkl.hantekgui;

import com.mkkl.hantekgui.protocol.OscilloscopeSampleRate;

public class OscilloscopeSettings {
    private static OscilloscopeSampleRate currentSampleRate;

    public static float getChartFpsLimit() {
        return chartFpsLimit;
    }

    public static void setChartFpsLimit(float chartFpsLimit) {
        OscilloscopeSettings.chartFpsLimit = chartFpsLimit;
    }

    private static float chartFpsLimit = 10;

    public static OscilloscopeSampleRate getCurrentSampleRate() {
        return currentSampleRate;
    }

    public static void setCurrentSampleRate(OscilloscopeSampleRate currentSampleRate) {
        OscilloscopeSettings.currentSampleRate = currentSampleRate;
    }
}
