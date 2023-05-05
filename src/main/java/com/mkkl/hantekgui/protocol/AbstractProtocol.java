package com.mkkl.hantekgui.protocol;

public interface AbstractProtocol {
    AbstractDevice[] getConnectedDevices();
    void connectDevice(AbstractDevice device);
    OscilloscopeChannel[] getChannels();
    OscilloscopeSampleRate[] getAvailableSampleRates();
    OscilloscopeVoltRanges[] getVoltageRanges();

    void setSampleRate(OscilloscopeSampleRate oscilloscopeSampleRate);
    void setVoltageRange(OscilloscopeChannel channel, OscilloscopeVoltRanges voltRanges);
    void setChannelActive(OscilloscopeChannel channel, boolean active);
    void startCapture();
    void stopCapture();

    AbstractDataReader getDataReader();
    AbstractByteBufferFormatter getDataFormatter(FormatterListener formatterListener);
}
