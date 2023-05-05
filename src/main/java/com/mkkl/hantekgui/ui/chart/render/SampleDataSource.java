package com.mkkl.hantekgui.ui.chart.render;

import com.mkkl.hantekgui.capture.SampleBatch;

import java.util.concurrent.CompletableFuture;

public interface SampleDataSource {
    CompletableFuture<SampleBatch> requestData();
}
