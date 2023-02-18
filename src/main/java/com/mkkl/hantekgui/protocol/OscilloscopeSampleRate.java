package com.mkkl.hantekgui.protocol;

public record OscilloscopeSampleRate(int id, long samplesPerSecond) {
    public float getTimeBetweenPoints() {
        return 1/(float)samplesPerSecond;
    }
}
