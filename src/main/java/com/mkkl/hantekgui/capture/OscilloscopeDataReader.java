package com.mkkl.hantekgui.capture;

import com.mkkl.hantekgui.protocol.DataReaderListener;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;

import javax.usb.UsbException;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

//Class used in separate thread to minimize gaps between readings
public class OscilloscopeDataReader implements Runnable{
    private final OscilloscopeCommunication scopeCommunication;
    private final PipedOutputStream pipedOutputStream = new PipedOutputStream();
    private final int maxPacketSize = 8192*2;//TODO implement
    public boolean realTimeCapture = false;

    public OscilloscopeDataReader(OscilloscopeCommunication scopeCommunication) {
        this.scopeCommunication = scopeCommunication;
//        maxPacketSize = scopeCommunication.getConnectedInterface().getUsbEndpoint(0).getUsbEndpointDescriptor().wMaxPacketSize();
    }

    public void pause() throws InterruptedException {
        wait();
    }

    //Resumes capture
    public void resume() {
        notify();
    }

    private boolean capture = true;
    public void stop() {
        capture = false;
        scopeCommunication.stopCapture();
    }

    @Override
    public void run() {
        scopeCommunication.startCapture();
        while(capture) {
            try {
                pipedOutputStream.write(scopeCommunication.syncRead((short) maxPacketSize));
            } catch (IOException | UsbException e) {
                //TODO handle exceptions
                e.printStackTrace();
            }
        }
    }
    public PipedOutputStream getPipedOutputStream() {
        return pipedOutputStream;
    }
}