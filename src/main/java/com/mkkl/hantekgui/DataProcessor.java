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
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.lang3.ArrayUtils;

public class DataProcessor implements AutoCloseable {
    private ScopeChart scopeChart;
    private OscilloscopeDataReader oscilloscopeDataReader;
    private final Thread readingThread;
    private final FloatDataSet[] dataSets;
    private final OscilloscopeCommunication scopeCommunication;
    private final float timeBetweenPoints;
    private final ArrayList<Float>[] samples;
    private final float[] pointX = new float[8192];

    public DataProcessor(OscilloscopeSettings oscilloscopeSettings, OscilloscopeCommunication scopeCommunication, ScopeChart scopeChart) {
        this.scopeCommunication = scopeCommunication;
        OscilloscopeDataReader oscilloscopeDataReader = new OscilloscopeDataReader(scopeCommunication);
        oscilloscopeDataReader.addEventListener(new DataReaderListener() {
            @Override
            public void onDataPackedReceived(byte[] data) {
                processPacket(data);
            }

            @Override
            public void onDataCompleted() {
                resetDataset();
            }
        });

        List<OscilloscopeChannel> oscilloscopeChannel = scopeCommunication.getChannels().stream().toList();

        dataSets = new FloatDataSet[oscilloscopeChannel.size()];
        samples = new ArrayList[2];
        for(int i = 0; i < scopeCommunication.getChannels().size(); i++) {
            dataSets[i] = new FloatDataSet(oscilloscopeChannel.get(i).name());
            samples[i] = new ArrayList<Float>(8192);
        }

        timeBetweenPoints = oscilloscopeSettings.getCurrentSampleRate().getTimeBetweenPoints()*10;
        float nextPointPos = 0;
        for(int i = 0; i < 8192; i++) {
            pointX[i] = nextPointPos;
            nextPointPos += timeBetweenPoints;
        }

        scopeChart.draw(dataSets[0]);
        scopeChart.draw(dataSets[1]);
        //TODO change timeBetweenPoints when sample rate changes

        readingThread = new Thread(oscilloscopeDataReader);
        readingThread.start();
    }
    private synchronized void processPacket(byte[] data) {
        scopeCommunication.processPacket(data, floats -> {
            samples[0].add(floats[0]);
            samples[1].add(floats[1]);

        });
    }

    private void resetDataset() {
        Arrays.stream(dataSets).forEach(FloatDataSet::clearData);
        for(int i = 0; i < scopeCommunication.getChannels().size(); i++) {
            dataSets[i].set(pointX, ArrayUtils.toPrimitive(samples[i].toArray(new Float[0]), 0.0F));
        }
    }

    @Override
    public void close() throws Exception {
        readingThread.join();
    }
}
