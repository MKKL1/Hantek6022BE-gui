package com.mkkl.hantekgui.capture;

import java.util.ArrayList;
import java.util.List;

public class DataReaderManager {
    private static final List<DataReceivedEvent> dataReceivedEventList = new ArrayList<DataReceivedEvent>();

    public static void register(DataReceivedEvent dataReceivedEvent) {
        dataReceivedEventList.add(dataReceivedEvent);
    }

    public static void unregister(DataReceivedEvent dataReceivedEvent) {
        dataReceivedEventList.remove(dataReceivedEvent);
    }

    //TODO make accessible only from data processor
    public static void fireDataReceivedEvent(SampleBatch sampleBatch) {
        dataReceivedEventList.forEach(x -> x.onDataReceived(sampleBatch));
    }
}
