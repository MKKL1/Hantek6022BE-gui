package com.mkkl.hantekgui.protocol;

import java.util.Collection;

public interface OscilloscopeCommunication {
    //TODO exceptions
    Collection<OscilloscopeDevice> getConnectedDevices() throws Exception;
    void connectDevice(OscilloscopeDevice device) throws Exception;
    Collection<OscilloscopeChannel> getChannels();
    Collection<OscilloscopeSampleRate> getAvailableSampleRates();
    Collection<OscilloscopeVoltRanges> getVoltageRanges();

    void setActiveChannel(OscilloscopeChannel channel);
    void setSampleRate(OscilloscopeSampleRate oscilloscopeSampleRate);
    void setVoltageRange(OscilloscopeChannel channel, OscilloscopeVoltRanges voltRanges);
    void setProbeMultiplier(OscilloscopeChannel channel, int value);
    //TODO calibration
    //TODO creating data reader
}
