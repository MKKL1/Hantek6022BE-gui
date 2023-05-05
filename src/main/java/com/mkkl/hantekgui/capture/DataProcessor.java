package com.mkkl.hantekgui.capture;

import com.mkkl.hantekapi.communication.adcdata.ADCDataFormatter;
import com.mkkl.hantekgui.AppConstants;
import com.mkkl.hantekgui.protocol.AbstractByteBufferFormatter;
import com.mkkl.hantekgui.protocol.AbstractProtocol;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DataProcessor extends Thread {
    private final BlockingQueue<ByteBuffer> bufferQueue = new LinkedBlockingQueue<>(64);
    private final int sampleBatchSize;
    private final AbstractByteBufferFormatter byteBufferFormatter;

    public DataProcessor(AbstractProtocol oscilloscopeCommunication) {
        super("Data Processor Thread");
        this.sampleBatchSize = AppConstants.packetSize/2;
        this.byteBufferFormatter = oscilloscopeCommunication.getDataFormatter(DataReaderManager::fireDataReceivedEvent);
    }

    public void receiveData(ByteBuffer buffer) {
        bufferQueue.add(buffer);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byteBufferFormatter.formatNext(bufferQueue.take());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
