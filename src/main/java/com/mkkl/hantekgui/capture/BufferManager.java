package com.mkkl.hantekgui.capture;

import java.nio.ByteBuffer;

public class BufferManager {
    private final ByteBuffer[] byteBufferArray;
    private final int arraySize;
    private int position = 0;
    public BufferManager(int arraySize, int bufferSize) {
        this.arraySize = arraySize;
        byteBufferArray = new ByteBuffer[arraySize];
        for(int i = 0; i < arraySize; i++) {
            byteBufferArray[i] = ByteBuffer.allocateDirect(bufferSize);
        }
    }

    public synchronized ByteBuffer getNext() {
        if(position >= arraySize) resetPosition();
        ByteBuffer byteBuffer = byteBufferArray[position];
        byteBuffer.clear();
        position++;
        return byteBuffer;
    }

    public synchronized void resetPosition() {
        position = 0;
    }

    //TODO temp
    public ByteBuffer[] getByteBufferArray() {
        return byteBufferArray;
    }
}
