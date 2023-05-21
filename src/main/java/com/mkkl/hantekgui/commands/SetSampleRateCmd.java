package com.mkkl.hantekgui.commands;

import com.mkkl.hantekgui.protocol.AbstractProtocol;
import com.mkkl.hantekgui.protocol.OscilloscopeSampleRate;
import com.mkkl.hantekgui.settings.SettingsRegistry;

public class SetSampleRateCmd implements Command {

    private final AbstractProtocol protocol;
    private final OscilloscopeSampleRate oscilloscopeSampleRate;

    public SetSampleRateCmd(AbstractProtocol protocol, OscilloscopeSampleRate oscilloscopeSampleRate) {
        this.protocol = protocol;
        this.oscilloscopeSampleRate = oscilloscopeSampleRate;
    }

    @Override
    public void execute() throws Exception {
        protocol.stopCapture();
        SettingsRegistry.currentSampleRate.setValue(oscilloscopeSampleRate);
        protocol.setSampleRate(oscilloscopeSampleRate);
        protocol.startCapture();
    }
}
