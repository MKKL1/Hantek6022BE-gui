package com.mkkl.hantekgui;

import com.mkkl.hantekgui.protocol.OscilloscopeSampleRate;

public class OscilloscopeSettings {
    private OscilloscopeSampleRate currentSampleRate;

    public OscilloscopeSampleRate getCurrentSampleRate() {
        return currentSampleRate;
    }

    public void setCurrentSampleRate(OscilloscopeSampleRate currentSampleRate) {
        this.currentSampleRate = currentSampleRate;
    }
}
