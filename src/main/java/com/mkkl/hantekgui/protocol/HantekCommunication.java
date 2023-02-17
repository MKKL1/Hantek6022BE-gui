package com.mkkl.hantekgui.protocol;

import com.mkkl.hantekapi.Oscilloscope;
import com.mkkl.hantekapi.OscilloscopeManager;
import com.mkkl.hantekapi.channel.ActiveChannels;
import com.mkkl.hantekapi.constants.SampleRates;
import com.mkkl.hantekapi.constants.VoltageRange;

import javax.usb.UsbException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class HantekCommunication implements OscilloscopeCommunication {

    private Oscilloscope oscilloscope;
    private final boolean[] channelsActive = new boolean[2];

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

    public Oscilloscope getOscilloscope() {
        return oscilloscope;
    }
}
