package com.mkkl.hantekgui.protocol;

import java.nio.ByteBuffer;

public abstract class AbstractByteBufferFormatter {
    protected final FormatterListener formatterListener;

    protected AbstractByteBufferFormatter(FormatterListener formatterListener) {
        this.formatterListener = formatterListener;
    }

    public abstract void formatNext(ByteBuffer buffer);
}
