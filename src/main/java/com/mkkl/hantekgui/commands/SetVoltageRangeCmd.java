package com.mkkl.hantekgui.commands;

import com.mkkl.hantekgui.protocol.AbstractProtocol;
import com.mkkl.hantekgui.protocol.OscilloscopeChannel;
import com.mkkl.hantekgui.protocol.OscilloscopeSampleRate;
import com.mkkl.hantekgui.protocol.OscilloscopeVoltRanges;
import com.mkkl.hantekgui.settings.SettingsRegistry;

public class SetVoltageRangeCmd implements Command {

    private final AbstractProtocol protocol;
    private final OscilloscopeChannel channel;
    private final OscilloscopeVoltRanges oscilloscopeVoltRanges;

    public SetVoltageRangeCmd(AbstractProtocol protocol, OscilloscopeChannel channel, OscilloscopeVoltRanges oscilloscopeVoltRanges) {
        this.protocol = protocol;
        this.channel = channel;
        this.oscilloscopeVoltRanges = oscilloscopeVoltRanges;
    }

    @Override
    public void execute() throws Exception {
        protocol.stopCapture();
        protocol.setVoltageRange(channel, oscilloscopeVoltRanges);
        protocol.startCapture();
    }
}
