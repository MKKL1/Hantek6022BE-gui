package com.mkkl.hantekgui.protocol;

import com.mkkl.hantekgui.capture.SampleBatch;

public interface FormatterListener {
    void onFormatted(SampleBatch sampleBatch);
}
