package com.mkkl.hantekgui.settings;

import com.mkkl.hantekgui.protocol.OscilloscopeSampleRate;

public class SettingsRegistry {
    public static final SettingsAttribute<OscilloscopeSampleRate> currentSampleRate = new SettingsAttribute<>();
    public static final SettingsAttribute<Float> chartFpsLimit = new SettingsAttribute<>(10f);
    public static final SettingsAttribute<Integer> sampleCountPerFrame = new SettingsAttribute<>(10000);
}
