package com.mkkl.hantekgui.ui.chart.render;

import com.mkkl.hantekgui.capture.SampleBatch;

public interface SamplesRenderedListener {
    void onRenderTick(SampleBatch batchToRender);
}
