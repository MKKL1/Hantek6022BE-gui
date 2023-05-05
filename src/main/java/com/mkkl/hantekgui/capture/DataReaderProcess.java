package com.mkkl.hantekgui.capture;

import com.mkkl.hantekgui.AppConstants;
import com.mkkl.hantekgui.protocol.AbstractDataReader;
import com.mkkl.hantekgui.protocol.AbstractProtocol;

//Class used in separate thread to minimize gaps between readings
public class DataReaderProcess extends Thread {
    private final AbstractProtocol scopeCommunication;
    private final DataProcessor dataProcessor;
    private final short packetSize;

    private final AbstractDataReader abstractDataReader;


    public DataReaderProcess(AbstractProtocol scopeCommunication, DataProcessor dataProcessor) {
        super("Data Reader Thread");
        this.scopeCommunication = scopeCommunication;
        this.dataProcessor = dataProcessor;

        //TODO calculate by multiple of max packet size of endpoint
        packetSize = AppConstants.packetSize;
        abstractDataReader = scopeCommunication.getDataReader();
        abstractDataReader.initialize(dataProcessor::receiveData);
    }

    @Override
    public void run() {
        scopeCommunication.startCapture();
        while(!currentThread().isInterrupted()) {
            try {
                abstractDataReader.loop();
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
}