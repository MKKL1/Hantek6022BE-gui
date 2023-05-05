package com.mkkl.hantekgui.protocol;

public interface AbstractDataReader {
    void initialize(DataReaderListener dataReaderListener);
    void loop() throws InterruptedException;
}
