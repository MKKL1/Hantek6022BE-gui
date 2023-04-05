package com.mkkl.hantekgui.protocol;

import com.mkkl.hantekapi.Oscilloscope;
import com.mkkl.hantekapi.ScopeUtils;
import com.mkkl.hantekapi.channel.ActiveChannels;
import com.mkkl.hantekapi.channel.ChannelManager;
import com.mkkl.hantekapi.channel.ScopeChannel;
import com.mkkl.hantekapi.communication.adcdata.ADCDataFormatter;
import com.mkkl.hantekapi.communication.adcdata.AdcInputStream;
import com.mkkl.hantekapi.communication.readers.async.AsyncScopeDataReader;
import com.mkkl.hantekapi.communication.readers.async.ReuseTransferAsyncReader;
import com.mkkl.hantekapi.constants.HantekDevices;
import com.mkkl.hantekapi.constants.SampleRates;
import com.mkkl.hantekapi.constants.VoltageRange;
import com.mkkl.hantekapi.devicemanager.OscilloscopeManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//Implementation of hantek communication protocol
//Only class that should use hantek api library,
// in other parts of the project interface should be used instead
public class HantekCommunication implements OscilloscopeCommunication {
    private Oscilloscope oscilloscope;
    private final boolean[] channelsActive = new boolean[2];
    private ScopeChannel[] channels;
    private ChannelManager channelManager;
    private int packetSize;

    @Override
    public Collection<OscilloscopeDevice> getConnectedDevices() throws Exception {
        List<OscilloscopeDevice> deviceList = new ArrayList<>();
        OscilloscopeManager.findSupportedDevices().getConnections().forEach(x -> {
            deviceList.add(new OscilloscopeDevice(x.oscilloscope().toString(), x));
        });

        return deviceList;
    }

    @Override
    public void connectDevice(OscilloscopeDevice device) throws Exception {
//        oscilloscope = device.deviceRecord().oscilloscope();
        oscilloscope = ScopeUtils.getAndFlashFirmware(HantekDevices.DSO6022BE);
        oscilloscope.setup();
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
    public void startCapture() {
        oscilloscope.startCapture();
    }

    @Override
    public void stopCapture() {
        oscilloscope.stopCapture();
    }

    @Override
    public AsyncScopeDataReader getAsyncReader() {
        return new AsyncScopeDataReader(oscilloscope, 5);
    }

    @Override
    public ReuseTransferAsyncReader getReuseAsyncReader(int bufferSize, int savedTransfers) {
        return new ReuseTransferAsyncReader(oscilloscope, bufferSize, savedTransfers, 5);
    }


    @Override
    public byte[] readSample(InputStream stream) throws IOException {
        byte[] data = new byte[2];
        data[0] = (byte) stream.read();
        data[1] = (byte) stream.read();
        return data;
    }

    @Override
    public ADCDataFormatter getAdcDataFormatter() {
        return new ADCDataFormatter(channelManager);
    }

    @Override
    public AdcInputStream getAdcInputStream(InputStream inputStream) {
        return new AdcInputStream(inputStream, channelManager, packetSize);
    }

    @Override
    public short getPacketSize() {
        return oscilloscope.getScopeInterface().getEndpoint().getMaxPacketSize();
    }

    public Oscilloscope getOscilloscope() {
        return oscilloscope;
    }
}
