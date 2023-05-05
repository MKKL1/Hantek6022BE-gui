package com.mkkl.hantekgui.capture;

import java.util.Arrays;

public class SampleBatch {
    private float[] ch1Data;
    private float[] ch2Data;
    public int length;

    public SampleBatch(float[] ch1Data, float[] ch2Data) {
        this.ch1Data = ch1Data;
        this.ch2Data = ch2Data;
        if (ch1Data.length != ch2Data.length) throw new RuntimeException("Array lengths are not equal");
        this.length = ch1Data.length;
    }

    public float[] getCh1Data() {
        return ch1Data;
    }

    public float[] getCh2Data() {
        return ch2Data;
    }

    public void concatenate(SampleBatch sampleBatch2, int sizeToAdd) {
        int oldlength = length;
        ch1Data = Arrays.copyOf(ch1Data, ch1Data.length + sizeToAdd);
        ch2Data = Arrays.copyOf(ch2Data, ch2Data.length + sizeToAdd);
        length = ch1Data.length;
        System.arraycopy(sampleBatch2.ch1Data, 0, ch1Data, oldlength, sizeToAdd);
        System.arraycopy(sampleBatch2.ch2Data, 0, ch2Data, oldlength, sizeToAdd);
    }

    public void concatenate(SampleBatch sampleBatch2) {
        concatenate(sampleBatch2, sampleBatch2.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleBatch that = (SampleBatch) o;
        return Arrays.equals(ch1Data, that.ch1Data) && Arrays.equals(ch2Data, that.ch2Data);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(ch1Data);
        result = 31 * result + Arrays.hashCode(ch2Data);
        return result;
    }
}
