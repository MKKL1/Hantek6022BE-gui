package com.mkkl.hantekgui.capture;

import com.mkkl.hantekgui.AppConstants;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class CaptureHistory {
    private final CircularFifoQueue<SamplesBatch> circularFifoQueue;
    final Object object = new Object();
    private int toReadCount = 0;

    private static CaptureHistory instance;

    public CaptureHistory(int size) {
        circularFifoQueue = new CircularFifoQueue<>(size);
        DataReaderManager.register(samplesBatch -> {
            circularFifoQueue.add(samplesBatch);
            if(toReadCount > 0) {
                toReadCount -= samplesBatch.length;
                synchronized (object) {
                    if (toReadCount <= 0) {
                        object.notify();
                    }
                }
            }

        });
    }

    public SamplesBatch[] getSamples(int countOfSamples) {
        ArrayList<SamplesBatch> batchArrayList = new ArrayList<>();
        int toRead = countOfSamples;
        for(SamplesBatch samplesBatch : circularFifoQueue) {
            batchArrayList.add(samplesBatch);
            toRead -= samplesBatch.length;
            if(toRead <= 0) break;
        }
        return batchArrayList.toArray(new SamplesBatch[0]);
    }

    public SamplesBatch[] getNewSamples(int countOfSamples) throws InterruptedException {
        if (countOfSamples == 0) return null;
        toReadCount = countOfSamples;
        synchronized (object) {
            object.wait();
            return getSamples(countOfSamples);
        }
    }

    public static CaptureHistory getInstance() {
        if(instance == null) instance = new CaptureHistory(AppConstants.captureHistorySize);
        return instance;
    }
}
