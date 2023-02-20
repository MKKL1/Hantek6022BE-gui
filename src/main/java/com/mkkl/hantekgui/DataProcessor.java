package com.mkkl.hantekgui;

import com.mkkl.hantekapi.communication.adcdata.AdcInputStream;
import com.mkkl.hantekgui.protocol.DataReaderListener;
import com.mkkl.hantekgui.protocol.OscilloscopeChannel;
import com.mkkl.hantekgui.protocol.OscilloscopeCommunication;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.dataset.spi.FloatDataSet;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class DataProcessor implements Runnable, AutoCloseable {
    private ScopeChart scopeChart;
    private final OscilloscopeDataReader oscilloscopeDataReader;
    private Thread readingThread;
    private final OscilloscopeCommunication scopeCommunication;
    private final float timeBetweenPoints;
    private final int channelCount;
    private final FloatDataSet[] dataSets;
    private final ArrayList<Float>[] samples;
    private final float[] xAxisPoints;
    private PipedInputStream pipedInputStream;
    //TODO remove dependance on hantek api library
    private AdcInputStream adcInputStream;
    private int currentSampleCount = 0;
    AnimationTimer timer;
    private boolean shouldUpdate = false;
    float[] copych1;
    float[] copych2;
    private final int datapointCount = 10*1024;

    long lastTimeStamp = 0;

    public DataProcessor(OscilloscopeSettings oscilloscopeSettings, OscilloscopeCommunication scopeCommunication, ScopeChart scopeChart) {
        this.scopeCommunication = scopeCommunication;
        this.scopeChart = scopeChart;

        oscilloscopeDataReader = new OscilloscopeDataReader(scopeCommunication);
        List<OscilloscopeChannel> oscilloscopeChannel = scopeCommunication.getChannels().stream().toList();
        channelCount = oscilloscopeChannel.size();
        dataSets = new FloatDataSet[channelCount];
        samples = new ArrayList[channelCount];
        for(int i = 0; i < channelCount; i++) {
            dataSets[i] = new FloatDataSet(oscilloscopeChannel.get(i).name());
            samples[i] = new ArrayList<Float>(datapointCount);
        }
        timeBetweenPoints = oscilloscopeSettings.getCurrentSampleRate().getTimeBetweenPoints();
        xAxisPoints = new float[datapointCount];
        for(int i = 0; i < datapointCount; i++) {
            xAxisPoints[i] = i*timeBetweenPoints;
        }

        scopeChart.getDatasets().addAll(dataSets);


        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if(shouldUpdate) {
                    double frameDuration = (now - lastTimeStamp) / 1e9;
                    lastTimeStamp = now;
                    System.out.println(1/frameDuration);
                    dataSets[0].set(xAxisPoints, copych1);
                    dataSets[1].set(xAxisPoints, copych2);
                    shouldUpdate = false;
                }
            }
        };
        timer.start();
    }

    @Override
    public void run() {
        pipedInputStream = new PipedInputStream();
        try {
            oscilloscopeDataReader.getPipedOutputStream().connect(pipedInputStream);
            adcInputStream = scopeCommunication.getAdcInputStream(pipedInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        readingThread = new Thread(oscilloscopeDataReader);
        readingThread.start();

        while(!Thread.currentThread().isInterrupted()) {
            try {
                float[] formattedSample = adcInputStream.readFormattedVoltages();
                samples[0].add(formattedSample[0]);
                samples[1].add(formattedSample[1]);
                currentSampleCount++;
                if(currentSampleCount >= datapointCount) {
                    copych1 = ArrayUtils.toPrimitive(samples[0].toArray(new Float[0]), 0.0F);
                    copych2 = ArrayUtils.toPrimitive(samples[1].toArray(new Float[0]), 0.0F);
                    shouldUpdate = true;
                    samples[0].clear();
                    samples[1].clear();
                    currentSampleCount = 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws Exception {
        oscilloscopeDataReader.stop();
        pipedInputStream.close();
        readingThread.join();
    }
}
