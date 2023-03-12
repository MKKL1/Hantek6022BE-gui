package com.mkkl.hantekgui.protocol;

import com.mkkl.hantekapi.Oscilloscope;
import com.mkkl.hantekapi.OscilloscopeManager;
import com.mkkl.hantekapi.channel.ActiveChannels;
import com.mkkl.hantekapi.channel.ChannelManager;
import com.mkkl.hantekapi.channel.ScopeChannel;
import com.mkkl.hantekapi.communication.adcdata.AdcInputStream;
import com.mkkl.hantekapi.communication.adcdata.ScopeDataReader;
import com.mkkl.hantekapi.constants.SampleRates;
import com.mkkl.hantekapi.constants.VoltageRange;

import javax.usb.UsbException;
import javax.usb.UsbInterface;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//Implementation of hantek communication protocol
//Only class that should use hantek api library,
// in other parts of the project interface should be used instead
public class HantekCommunication implements OscilloscopeCommunication {
    private Oscilloscope oscilloscope;
    private final boolean[] channelsActive = new boolean[2];
    private ScopeChannel[] channels;
    private ScopeDataReader scopeDataReader;
    private ChannelManager channelManager;
    private int packetSize;

    @Override
    public Collection<OscilloscopeDevice> getConnectedDevices() throws Exception {
        List<OscilloscopeDevice> deviceList = new ArrayList<>();
        OscilloscopeManager.findSupportedDevices().forEach((key, value) -> {
            try {
                deviceList.add(new OscilloscopeDevice(key.getProductString(), key.getParentUsbPort().toString(), key.getParentUsbPort().getPortNumber()));
            } catch (UsbException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        return deviceList;
    }

    @Override
    public void connectDevice(OscilloscopeDevice device) throws Exception {
        oscilloscope = OscilloscopeManager.connections.entrySet()
                .stream()
                .filter(entry -> device.portId() == entry.getKey().getParentUsbPort().getPortNumber())
                .findFirst()
                .orElseThrow()
                .getValue();

        if (!oscilloscope.isFirmwarePresent()) {
            oscilloscope.flash_firmware();
            while(oscilloscope == null || !oscilloscope.isFirmwarePresent()) {
                Thread.sleep(100);
                oscilloscope = OscilloscopeManager.connections.entrySet()
                        .stream()
                        .filter(entry -> device.portId() == entry.getKey().getParentUsbPort().getPortNumber())
                        .findFirst()
                        .orElseThrow()
                        .getValue();
                System.out.print('.');
            }
        }

        oscilloscope.setup();

        scopeDataReader = oscilloscope.createDataReader();
        channels = oscilloscope.getChannels().toArray(new ScopeChannel[0]);
        channelManager = oscilloscope.getChannelManager();
        packetSize = oscilloscope.getScopeInterface().getEndpoint().getPacketSize();
    }

    @Override
    public Collection<OscilloscopeChannel> getChannels() {
        List<OscilloscopeChannel> channels = new ArrayList<>();
        channels.add(new OscilloscopeChannel(0, "Channel 1"));
        channels.add(new OscilloscopeChannel(1, "Channel 2"));
        return channels;
    }

    @Override
    public Collection<OscilloscopeSampleRate> getAvailableSampleRates() {
        List<OscilloscopeSampleRate> sampleRates = new ArrayList<>();
        for(SampleRates sr : SampleRates.values()){
            if(!sr.isSingleChannel()) sampleRates.add(new OscilloscopeSampleRate(sr.getSampleRateId(), sr.getSampleCount()));
            else if(oscilloscope.getChannelManager().getActiveChannels().getActiveCount()==1) sampleRates.add(new OscilloscopeSampleRate(sr.getSampleRateId(), sr.getSampleCount()));
        }
        return sampleRates;
    }

    @Override
    public Collection<OscilloscopeVoltRanges> getVoltageRanges() {
        return Arrays.stream(VoltageRange.values())
                .map(x -> new OscilloscopeVoltRanges(x.getGainId(), x.getGainMiliV()))
                .collect(Collectors.toList());
    }

    @Override
    public void setActiveChannel(OscilloscopeChannel channel) {
        channelsActive[channel.id()] = true;
        if(channelsActive[0] && channelsActive[1]) oscilloscope.setActiveChannels(ActiveChannels.CH1CH2);
        else if (!channelsActive[1]) oscilloscope.setActiveChannels(ActiveChannels.CH1);
    }

    @Override
    public void setSampleRate(OscilloscopeSampleRate oscilloscopeSampleRate) {
        oscilloscope.setSampleRate((byte) oscilloscopeSampleRate.id());
    }

    @Override
    public void setVoltageRange(OscilloscopeChannel channel, OscilloscopeVoltRanges voltRanges) {
        oscilloscope.getChannel(channel.id()).setVoltageRange(voltRanges.id());
    }

    @Override
    public void setProbeMultiplier(OscilloscopeChannel channel, int value) {
        oscilloscope.getChannel(channel.id()).setProbeMultiplier(value);
    }

    @Override
    public UsbInterface getConnectedInterface() {
        return oscilloscope.getScopeInterface().getUsbInterface();
    }

    @Override
    public void startCapture() {
        scopeDataReader.startCapture();
    }

    @Override
    public void stopCapture() {
        scopeDataReader.stopCapture();
    }

    @Override
    public CompletableFuture<Void> asyncRead(short size, Consumer<byte[]> packetConsumer) {
        return scopeDataReader.asyncRead(size, packetConsumer);
    }

    @Override
    public byte[] syncRead(short size) throws IOException, UsbException {
        return scopeDataReader.syncRead(size);
    }

    @Override
    public byte[] readSample(InputStream stream) throws IOException {
        byte[] data = new byte[2];
        data[0] = (byte) stream.read();
        data[1] = (byte) stream.read();
        return data;
    }

    @Override
    public float formatRawData(OscilloscopeChannel channel, byte raw) {
        return channels[channel.id()].formatData(raw);
    }

    @Override
    public float[] formatChannelsData(byte[] raw) {
        return new float[] {
                channels[0].formatData(raw[0]),
                channels[1].formatData(raw[1])
        };
    }

    @Override
    public void processPacket(byte[] data, Consumer<float[]> consumer) {
        int bytesToRead = data.length;
        AdcInputStream inputStream = new AdcInputStream(new ByteArrayInputStream(data), channelManager, packetSize);
        try {
            while(bytesToRead > 0) {
                float[] channelData = inputStream.readFormattedVoltages();
                consumer.accept(channelData);
                bytesToRead -= 2;
            }
        } catch (EOFException ignored) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AdcInputStream getAdcInputStream(InputStream inputStream) {
        return new AdcInputStream(inputStream, channelManager, packetSize);
    }

    public Oscilloscope getOscilloscope() {
        return oscilloscope;
    }
}
