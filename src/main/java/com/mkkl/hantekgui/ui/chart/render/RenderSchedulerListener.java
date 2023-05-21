package com.mkkl.hantekgui.ui.chart.render;

import com.mkkl.hantekgui.capture.SampleBatch;

public interface RenderSchedulerListener {
    void onRenderTick(SampleBatch batchToRender);
}
