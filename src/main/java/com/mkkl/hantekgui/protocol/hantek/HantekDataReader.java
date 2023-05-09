package com.mkkl.hantekgui.protocol.hantek;

import com.mkkl.hantekapi.OscilloscopeHandle;
import com.mkkl.hantekapi.communication.readers.BufferedCallback;
import com.mkkl.hantekapi.communication.readers.async.CachedAsyncReader;
import com.mkkl.hantekgui.protocol.AbstractDataReader;
import com.mkkl.hantekgui.protocol.DataReaderListener;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public class HantekDataReader implements AbstractDataReader {
    private final CachedAsyncReader cachedAsyncReader;
    private final AtomicInteger packetsInQueue = new AtomicInteger(0);
    final Object object = new Object();//TODO find clean way to synchronize

    public HantekDataReader(OscilloscopeHandle oscilloscopeHandle) {
        this.cachedAsyncReader = new CachedAsyncReader(oscilloscopeHandle, 512, 10, 5);
    }

    @Override
    public void initialize(DataReaderListener dataReaderListener) {
        cachedAsyncReader.registerListener(new BufferedCallback(false) {
            @Override
            public void onDataReceived(ByteBuffer byteBuffer) {
                dataReaderListener.receivePacket(byteBuffer);
                packetsInQueue.decrementAndGet();
                synchronized (object) {
                    object.notifyAll();
                }
            }
        });
    }

    @Override
    public void loop() throws InterruptedException {
        while(packetsInQueue.get() > 10)
            synchronized (object) {
                object.wait();
            }
        cachedAsyncReader.read();
        packetsInQueue.incrementAndGet();
    }
}
