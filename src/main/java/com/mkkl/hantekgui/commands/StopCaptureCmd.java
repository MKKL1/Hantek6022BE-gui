package com.mkkl.hantekgui.commands;

import com.mkkl.hantekgui.protocol.AbstractProtocol;

public class StopCaptureCmd implements Command {

    private final AbstractProtocol protocol;

    public StopCaptureCmd(AbstractProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void execute() throws Exception {
        protocol.stopCapture();
    }
}
