package com.mkkl.hantekgui.capture;

import com.mkkl.hantekgui.AppConstants;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.*;

public class CaptureHistory {
    private final CircularFifoQueue<SampleBatch> circularFifoQueue;
    private final int queueSize;
    private int newToRead = 0;

    private int waitForCount = 0;

    private static CaptureHistory instance;

    public CaptureHistory(int size) {
        circularFifoQueue = new CircularFifoQueue<>(size);
        this.queueSize = size;
        DataReaderManager.register(this::addSamples);
    }

    private void addSamples(SampleBatch sampleBatch) {
        synchronized (circularFifoQueue) {
            circularFifoQueue.add(sampleBatch);
        }
        if(newToRead < queueSize) newToRead++;
        if(waitForCount > 0) {
            waitForCount--;
            if(waitForCount == 0) {
                synchronized (this) {
                    notify();
                }
            }
        }
    }

    public void clear() {
        circularFifoQueue.clear();
        newToRead = 0;
        synchronized (this) {
            notify();
        }
    }

    public SampleBatch[] getSamples(int size) {
        ArrayList<SampleBatch> batchArrayList = new ArrayList<>(size);
        synchronized (circularFifoQueue) {
            Iterator<SampleBatch> iterator = circularFifoQueue.iterator();
            try {
                for (int i = 0; i < size; i++) {
                    batchArrayList.add(iterator.next());
                }
            } catch (Exception e) {
                System.out.println(circularFifoQueue.size());
                System.out.println(size);
                throw e;
            }
        }
        return batchArrayList.toArray(new SampleBatch[0]);
    }

    public SampleBatch[] getNewSamples(int size) throws InterruptedException {
        waitForCount = size-newToRead;
        if(waitForCount > 0) {
            synchronized (this) {
                wait();
            }
        }
        newToRead = 0;
        return getSamples(size);
    }

    public int getCurrentQueueSize() {
        return circularFifoQueue.size();
    }

    public static CaptureHistory getInstance() {
        if(instance == null) instance = new CaptureHistory(AppConstants.captureHistorySize);
        return instance;
    }
}
