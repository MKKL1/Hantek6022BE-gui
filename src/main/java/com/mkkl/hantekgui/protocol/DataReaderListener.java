package com.mkkl.hantekgui.protocol;

import java.nio.ByteBuffer;

public interface DataReaderListener {
    void receivePacket(ByteBuffer byteBuffer);
}
