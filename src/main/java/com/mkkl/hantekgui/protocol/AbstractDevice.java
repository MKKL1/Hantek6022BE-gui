package com.mkkl.hantekgui.protocol;

public abstract class AbstractDevice {
    protected final String name;

    protected AbstractDevice(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
