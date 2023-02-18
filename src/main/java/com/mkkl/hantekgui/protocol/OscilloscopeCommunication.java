package com.mkkl.hantekgui.protocol;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
    void startCapture();
    void stopCapture();
    CompletableFuture<Void> asyncRead(short size, Consumer<byte[]> packetConsumer);
    byte[] readSample(InputStream stream) throws IOException;
    float formatRawData(OscilloscopeChannel channel, byte raw);
    float[] formatChannelsData(byte[] raw);
    void processPacket(byte[] data, Consumer<float[]> consumer);
}
