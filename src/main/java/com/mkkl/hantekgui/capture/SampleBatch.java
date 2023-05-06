package com.mkkl.hantekgui.capture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class SampleBatch {
    private final ArrayList<ArrayList<float[]>> data;
    public int length;
    private final int channelsCount;

    public SampleBatch(float[]... channelData) {
        int length1 = channelData[0].length;
        channelsCount = channelData.length;
        for(int i = 1; i < channelsCount; i++)
            if(length1 != channelData[i].length) throw new RuntimeException("Array lengths are not equal");

        data = new ArrayList<>(channelsCount);
        for (int i = 0; i < channelsCount; i++) {
            ArrayList<float[]> arrayList = new ArrayList<>();
            arrayList.add(channelData[i]);
            data.set(i, arrayList);
        }
        length = length1;
    }

    public void addSamples(float[]... channelData) {
        if (channelData.length != channelsCount) throw new RuntimeException("Channel count is not equal");
        int length1 = channelData[0].length;
        for(int i = 1; i < channelsCount; i++)
            if(length1 != channelData[i].length) throw new RuntimeException("Array lengths are not equal");

        for (int i = 0; i < channelsCount; i++) {
            data.get(i).add(channelData[i]);
        }
        length += length1;
    }

    public void addSamples(SampleBatch sampleBatch) {
        if (sampleBatch.channelsCount != channelsCount) throw new RuntimeException("Channel count is not equal");

        for (int i = 0; i < channelsCount; i++) {
            data.get(i).addAll(sampleBatch.data.get(i));
        }

        length += sampleBatch.length;
    }

    public void addSamples(SampleBatch sampleBatch, int lengthToCopy) {
        if (sampleBatch.channelsCount != channelsCount) throw new RuntimeException("Channel count is not equal");

        int lengthRemaining = lengthToCopy;
        for (int i = 0; i < channelsCount; i++) {
            ArrayList<float[]> currentChannel = data.get(i);

            for (float[] dataToCopy : sampleBatch.data.get(i)) {
                if (dataToCopy.length <= lengthRemaining) {
                    currentChannel.add(dataToCopy);
                    lengthRemaining -= dataToCopy.length;
                } else {
                    float[] dataShortened = Arrays.copyOf(dataToCopy, lengthRemaining);
                    currentChannel.add(dataShortened);
                    lengthRemaining = 0;
                }
                if (lengthRemaining <= 0) break;
            }
        }

        length += sampleBatch.length;
    }

    public Iterator<Float> getChannelDataIterator(int channelId) {
        ArrayList<float[]> channelData = data.get(channelId);
        return new BatchChannelIterator(channelData);
    }


}

class BatchChannelIterator implements Iterator<Float> {
    private final ArrayList<float[]> channelData;
    private final int channelDataLength;
    private float[] currentArray;
    int currentListNumber = 0;
    int currentArrayNumber = 0;
    public BatchChannelIterator(ArrayList<float[]> channelData) {
        this.channelData = channelData;
        currentArray = channelData.get(0);
        channelDataLength = channelData.size();
    }

    @Override
    public boolean hasNext() {
        if(currentArrayNumber+1 >= currentArray.length) {
            return currentListNumber + 1 < channelDataLength;
        }
        return true;
    }

    @Override
    public Float next() {
        float currentValue = currentArray[currentArrayNumber];
        currentArrayNumber++;
        if(currentArrayNumber >= currentArray.length) {
            currentListNumber++;
            currentArray = channelData.get(currentListNumber);
        }
        return currentValue;
    }
}