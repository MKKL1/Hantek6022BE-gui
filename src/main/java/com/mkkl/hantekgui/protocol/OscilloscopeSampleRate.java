package com.mkkl.hantekgui.protocol;

public record OscilloscopeSampleRate(int id, long samplesPerSecond) {
    public float getTimeBetweenPoints() {
        return 1/(float)samplesPerSecond;
    }

    @Override
    public String toString() {
        return id + ":" + withSuffix(samplesPerSecond) + "S/s";
    }

    //TODO move
    private static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp-1));
    }
}
