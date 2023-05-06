package com.mkkl.hantekgui.protocol.hantek;

import com.mkkl.hantekapi.OscilloscopeHandle;
import com.mkkl.hantekapi.communication.adcdata.ADCDataFormatter;
import com.mkkl.hantekgui.protocol.AbstractBufferFormatter;
import com.mkkl.hantekgui.protocol.FormatterListener;

import java.nio.ByteBuffer;

public class HantekFormatter extends AbstractBufferFormatter {
    private final OscilloscopeHandle oscilloscopeHandle;
    private FormatterMode formatterMode;

    public HantekFormatter(OscilloscopeHandle oscilloscopeHandle, FormatterListener formatterListener) {
        super(formatterListener);
        this.oscilloscopeHandle = oscilloscopeHandle;
        onActiveChannelChange();
    }

    @Override
    public void onActiveChannelChange() {
        boolean singleMode = oscilloscopeHandle.getChannelManager().getActiveChannels().isSingleMode();
        ADCDataFormatter adcDataFormatter = ADCDataFormatter.create(oscilloscopeHandle.getChannelManager());

        if(singleMode) formatterMode = new SingleModeFormatter(adcDataFormatter, formatterListener);
        else formatterMode = new DoubleModeFormatter(adcDataFormatter, formatterListener);
    }

    @Override
    public void formatNext(ByteBuffer buffer) {
        formatterMode.formatNext(buffer);
    }
}
