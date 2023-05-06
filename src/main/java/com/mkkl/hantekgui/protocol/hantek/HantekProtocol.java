package com.mkkl.hantekgui.protocol.hantek;

import com.mkkl.hantekapi.Oscilloscope;
import com.mkkl.hantekapi.OscilloscopeHandle;
import com.mkkl.hantekapi.channel.ActiveChannels;
import com.mkkl.hantekapi.communication.adcdata.ADCDataFormatter;
import com.mkkl.hantekapi.constants.SampleRate;
import com.mkkl.hantekapi.constants.VoltageRange;
import com.mkkl.hantekapi.devicemanager.OscilloscopeManager;
import com.mkkl.hantekgui.protocol.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class HantekProtocol implements AbstractProtocol {
    private Oscilloscope oscilloscope;
    private OscilloscopeHandle oscilloscopeHandle;
    private boolean singleChannelMode = false;
    private boolean[] activeChannel = new boolean[2];

    @Override
    public AbstractDevice[] getConnectedDevices() {
        LinkedList<HantekDevice> hantekDevices = new LinkedList<>();
        OscilloscopeManager.findSupportedDevices().getConnections().forEach(x -> hantekDevices.add(new HantekDevice(x)));
        return hantekDevices.toArray(new HantekDevice[0]);
    }

    @Override
    public void connectDevice(AbstractDevice device) {
        if (device.getClass() != HantekDevice.class) throw new IllegalArgumentException("Device should be instance of HantekDevice");
        HantekDevice hantekDevice = (HantekDevice) device;

        oscilloscope = hantekDevice.getHantekDeviceRecord().oscilloscope();
        oscilloscopeHandle = oscilloscope.setup();
    }

    @Override
    public OscilloscopeChannel[] getChannels() {
        ArrayList<OscilloscopeChannel> channels = new ArrayList<>();
        oscilloscopeHandle.getChannels().forEach(x -> channels.add(new OscilloscopeChannel(x.getId().getChannelId(), x.toString())));
        return channels.toArray(new OscilloscopeChannel[0]);
    }

    @Override
    public OscilloscopeSampleRate[] getAvailableSampleRates() {
        return Arrays.stream(SampleRate.values())
                .filter(x -> x.isSingleChannel() && singleChannelMode)
                .map(sampleRate -> new OscilloscopeSampleRate(sampleRate.getSampleRateId(), sampleRate.getSampleCount()))
                .toArray(OscilloscopeSampleRate[]::new);
    }

    @Override
    public OscilloscopeVoltRanges[] getVoltageRanges() {
        return Arrays.stream(VoltageRange.values())
                .map(voltageRange -> new OscilloscopeVoltRanges(voltageRange.getGainId(), voltageRange.getGainMiliV()))
                .toArray(OscilloscopeVoltRanges[]::new);
    }

    @Override
    public void setSampleRate(OscilloscopeSampleRate oscilloscopeSampleRate) {
        oscilloscopeHandle.setSampleRate((byte) oscilloscopeSampleRate.id());
    }

    @Override
    public void setVoltageRange(OscilloscopeChannel channel, OscilloscopeVoltRanges voltRanges) {
        oscilloscopeHandle.getChannel(channel.getId()).setVoltageRange(voltRanges.id());
    }

    @Override
    public void setChannelActive(OscilloscopeChannel channel, boolean active) {
        activeChannel[channel.getId()] = active;
        if (activeChannel[1]) oscilloscopeHandle.setActiveChannels(ActiveChannels.CH1CH2);
        else oscilloscopeHandle.setActiveChannels(ActiveChannels.CH1);
    }

    @Override
    public void startCapture() {
        oscilloscopeHandle.startCapture();
    }

    @Override
    public void stopCapture() {
        oscilloscopeHandle.stopCapture();
    }

    @Override
    public AbstractDataReader getDataReader() {
        return new HantekDataReader(oscilloscopeHandle);
    }

    @Override
    public AbstractBufferFormatter getDataFormatter(FormatterListener formatterListener) {
        return new HantekFormatter(oscilloscopeHandle, formatterListener);
    }
}
