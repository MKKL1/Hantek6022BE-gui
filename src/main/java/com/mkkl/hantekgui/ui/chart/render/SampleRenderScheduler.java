package com.mkkl.hantekgui.ui.chart.render;

import com.mkkl.hantekgui.capture.SampleBatch;
import com.mkkl.hantekgui.settings.SettingsRegistry;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;

public class SampleRenderScheduler {
    private SampleBatch sampleBatch;
    private boolean shouldUpdate = false;
    private long updateTime;
    private final AnimationTimer timer;
    private final SampleDataSource sampleDataSource;
    private boolean started = false;

    private final List<SamplesRenderedListener> renderedListenerList = new ArrayList<>();

    public SampleRenderScheduler(SampleDataSource sampleDataSource) {
        this.sampleDataSource = sampleDataSource;
        //calculateUpdateTime(SettingsRegistry.chartFpsLimit.getValue());
        SettingsRegistry.chartFpsLimit.addAndActiveListener((oldValue, newValue) -> calculateUpdateTime(newValue));

        final long[] nextUpdate = {0};
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(shouldUpdate && now > nextUpdate[0]) {
                    //Send to render
                    renderedListenerList.forEach(x -> x.onRenderTick(sampleBatch));
                    shouldUpdate = false;
                    //Calculate next render time
                    nextUpdate[0] = now + updateTime;
                    //Request next data batch
                    requestData();
                }
            }
        };
    }

    private void requestData() {
        sampleDataSource.requestData().thenAccept(samples -> {
            sampleBatch = samples;
            shouldUpdate = true;
        });
    }

    private void calculateUpdateTime(float fpsLimit) {
        updateTime = (long) ((1/fpsLimit)*1e9);
    }

    public void registerListener(SamplesRenderedListener listener) {
        renderedListenerList.add(listener);
    }

    public void unregisterListener(SamplesRenderedListener listener) {
        renderedListenerList.remove(listener);
    }

    public void reset() {
        requestData();
    }

    public void start() {
        //Requesting first data frame
        requestData();
        if(!started) timer.start();
        started = true;
    }

    public void stop() {
        timer.stop();
        started = false;
    }
}
