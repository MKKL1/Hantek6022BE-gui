package com.mkkl.hantekgui.ui.chart.render;

import com.mkkl.hantekgui.capture.SampleBatch;
import com.mkkl.hantekgui.settings.SettingsRegistry;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SampleRenderScheduler {
    private SampleBatch sampleBatch;
    private boolean shouldUpdate = false;
    private long updateTime;
    private final AnimationTimer timer;
    private final SampleDataSource sampleDataSource;
    private boolean started = false;
    private long timeoutMS = 5000;

    private final List<RenderSchedulerListener> renderedListenerList = new ArrayList<>();

    public SampleRenderScheduler(SampleDataSource sampleDataSource) {
        this.sampleDataSource = sampleDataSource;
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
        sampleDataSource.requestData().orTimeout(timeoutMS, TimeUnit.MILLISECONDS).thenAccept(samples -> {
            sampleBatch = samples;
            shouldUpdate = true;
        });
    }

    private void calculateUpdateTime(float fpsLimit) {
        updateTime = (long) ((1/fpsLimit)*1e9);
    }

    public void registerListener(RenderSchedulerListener listener) {
        renderedListenerList.add(listener);
    }

    public void unregisterListener(RenderSchedulerListener listener) {
        renderedListenerList.remove(listener);
    }

    public long getTimeoutMS() {
        return timeoutMS;
    }

    public void setTimeoutMS(long timeoutMS) {
        this.timeoutMS = timeoutMS;
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
