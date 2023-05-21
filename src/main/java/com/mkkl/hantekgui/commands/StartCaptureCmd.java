package com.mkkl.hantekgui.commands;

import com.mkkl.hantekgui.protocol.AbstractProtocol;

public class StartCaptureCmd implements Command {

    private final AbstractProtocol protocol;

    public StartCaptureCmd(AbstractProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void execute() throws Exception {
        protocol.startCapture();
    }
}
