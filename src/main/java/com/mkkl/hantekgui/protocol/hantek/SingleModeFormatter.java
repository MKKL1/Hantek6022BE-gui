package com.mkkl.hantekgui.protocol.hantek;

import com.mkkl.hantekapi.communication.adcdata.ADCDataFormatter;
import com.mkkl.hantekgui.capture.SampleBatch;
import com.mkkl.hantekgui.protocol.FormatterListener;

import java.nio.ByteBuffer;

import static com.mkkl.hantekgui.AppConstants.sampleBatchSize;

public class SingleModeFormatter implements FormatterMode {
    private final ADCDataFormatter adcDataFormatter;
    private final FormatterListener formatterListener;

    public SingleModeFormatter(ADCDataFormatter adcDataFormatter, FormatterListener formatterListener) {
        this.adcDataFormatter = adcDataFormatter;
        this.formatterListener = formatterListener;
    }

    int samplesRead = 0;
    float[] ch1data = new float[sampleBatchSize];

    @Override
    public void formatNext(ByteBuffer byteBuffer) {
        while(byteBuffer.remaining() >= 1) {
            float[] values = adcDataFormatter.formatOneRawSample(new byte[] {byteBuffer.get()});
            ch1data[samplesRead] = values[0];
            samplesRead++;
            if(samplesRead >= sampleBatchSize) {
                formatterListener.onFormatted(new SampleBatch(ch1data));
                samplesRead = 0;
                ch1data = new float[sampleBatchSize];
            }
        }
    }
}
