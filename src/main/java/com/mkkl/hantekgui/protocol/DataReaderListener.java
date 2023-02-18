package com.mkkl.hantekgui.protocol;

public abstract class DataReaderListener {
    public void onDataPackedReceived(byte[] data) {}
    public void onDataCompleted() {}
}
