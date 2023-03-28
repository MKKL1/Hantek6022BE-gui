package com.mkkl.hantekgui.capture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

//Taken from https://stackoverflow.com/a/62258004
public class ByteBufferInputStream extends InputStream {

    ByteBuffer buf;

    public ByteBufferInputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    public ByteBufferInputStream() {

    }

    public void setBuf(ByteBuffer byteBuffer) {
        this.buf = byteBuffer;
    }

    public int read() throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }
        return buf.get() & 0xFF;
    }

    public int read(byte[] bytes, int off, int len)
            throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, buf.remaining());
        buf.get(bytes, off, len);
        return len;
    }
}