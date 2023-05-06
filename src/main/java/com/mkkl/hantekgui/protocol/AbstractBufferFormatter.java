package com.mkkl.hantekgui.protocol;

import java.nio.ByteBuffer;

public abstract class AbstractBufferFormatter {
    protected final FormatterListener formatterListener;

    protected AbstractBufferFormatter(FormatterListener formatterListener) {
        this.formatterListener = formatterListener;
    }

    public void onActiveChannelChange() {}

    public abstract void formatNext(ByteBuffer buffer);
}
