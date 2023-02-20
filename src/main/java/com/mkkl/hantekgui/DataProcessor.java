package com.mkkl.hantekgui;

import com.mkkl.hantekapi.communication.adcdata.AdcInputStream;
import com.mkkl.hantekgui.protocol.DataReaderListener;
import com.mkkl.hantekgui.protocol.OscilloscopeChannel;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.dataset.spi.FloatDataSet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.lang3.ArrayUtils;

public class DataProcessor implements Runnable,AutoCloseable {
    private ScopeChart scopeChart;
    private OscilloscopeDataReader oscilloscopeDataReader;
    private final Thread readingThread;
    private final OscilloscopeCommunication scopeCommunication;
    private final float timeBetweenPoints;
    private final int channelCount;
    private final BlockingQueue<byte[]> packetsToProcess = new LinkedBlockingQueue<>();
    private final FloatDataSet[] dataSets;

    public DataProcessor(OscilloscopeSettings oscilloscopeSettings, OscilloscopeCommunication scopeCommunication, ScopeChart scopeChart) {
        this.scopeCommunication = scopeCommunication;
        this.scopeChart = scopeChart;

        OscilloscopeDataReader oscilloscopeDataReader = new OscilloscopeDataReader(scopeCommunication);
        oscilloscopeDataReader.addEventListener(new DataReaderListener() {
            @Override
            public void onDataPackedReceived(byte[] data) {
                try {
                    packetsToProcess.put(data);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public void onDataCompleted() {

            }
        });

        List<OscilloscopeChannel> oscilloscopeChannel = scopeCommunication.getChannels().stream().toList();
        channelCount = oscilloscopeChannel.size();
        dataSets = new FloatDataSet[channelCount];
        timeBetweenPoints = oscilloscopeSettings.getCurrentSampleRate().getTimeBetweenPoints()*10;
        readingThread = new Thread(oscilloscopeDataReader);
        readingThread.start();
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                processPacket(packetsToProcess.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processPacket(byte[] data) {
        scopeCommunication.processPacket(data, new Consumer<float[]>() {
            @Override
            public void accept(float[] floats) {

            }
        });
    }

    @Override
    public void close() throws Exception {
        oscilloscopeDataReader.stop();
        readingThread.join();
    }
}
