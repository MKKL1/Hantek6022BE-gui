package com.mkkl.hantekgui.protocol.hantek;

import java.nio.ByteBuffer;

public interface FormatterMode {
    void formatNext(ByteBuffer byteBuffer);
}
