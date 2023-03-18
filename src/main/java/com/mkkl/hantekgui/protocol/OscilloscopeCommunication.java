package com.mkkl.hantekgui.protocol;

import com.mkkl.hantekapi.communication.adcdata.AdcInputStream;

import javax.usb.UsbException;
import javax.usb.UsbInterface;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

//Interface used to generalize oscilloscopes communication protocols
//Right now it is only used to communicate with hantek devices
//TODO cleanup
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
    UsbInterface getConnectedInterface();
    //TODO calibration
    void startCapture();
    void stopCapture();
    CompletableFuture<Void> asyncRead(short size, Consumer<byte[]> packetConsumer);

    byte[] syncRead(short size) throws IOException, UsbException;

    byte[] readSample(InputStream stream) throws IOException;
    float formatRawData(OscilloscopeChannel channel, byte raw);
    float[] formatChannelsData(byte[] raw);
    void processPacket(byte[] data, Consumer<float[]> consumer);
    AdcInputStream getAdcInputStream(InputStream inputStream);
    short getPacketSize();
}
