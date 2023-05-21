package com.mkkl.hantekgui.capture;

import java.util.ArrayList;
import java.util.List;

public class CaptureEventDispatcher {
    private final List<DataCaptureListener> listenerList = new ArrayList<DataCaptureListener>();

    public void register(DataCaptureListener listener) {
        listenerList.add(listener);
    }

    public void unregister(DataCaptureListener listener) {
        listenerList.remove(listener);
    }

    public List<DataCaptureListener> getListenerList() {
        return listenerList;
    }
}
