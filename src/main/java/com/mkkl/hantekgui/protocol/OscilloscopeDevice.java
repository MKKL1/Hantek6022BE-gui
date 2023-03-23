package com.mkkl.hantekgui.protocol;

import com.mkkl.hantekapi.devicemanager.HantekDeviceRecord;

public record OscilloscopeDevice(String name, HantekDeviceRecord deviceRecord) {
}
