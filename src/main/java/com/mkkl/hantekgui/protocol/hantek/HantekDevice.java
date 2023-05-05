package com.mkkl.hantekgui.protocol.hantek;

import com.mkkl.hantekapi.devicemanager.HantekDeviceRecord;
import com.mkkl.hantekgui.protocol.AbstractDevice;

public class HantekDevice extends AbstractDevice {
    private final HantekDeviceRecord hantekDeviceRecord;
    protected HantekDevice(HantekDeviceRecord hantekDeviceRecord) {
        super(hantekDeviceRecord.oscilloscope().toString());
        this.hantekDeviceRecord = hantekDeviceRecord;
    }

    public HantekDeviceRecord getHantekDeviceRecord() {
        return hantekDeviceRecord;
    }
}
