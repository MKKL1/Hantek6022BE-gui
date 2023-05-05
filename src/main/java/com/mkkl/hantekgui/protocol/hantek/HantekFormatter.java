package com.mkkl.hantekgui.protocol.hantek;

import com.mkkl.hantekapi.communication.adcdata.ADCDataFormatter;
import com.mkkl.hantekgui.capture.SampleBatch;
import com.mkkl.hantekgui.protocol.AbstractByteBufferFormatter;
import com.mkkl.hantekgui.protocol.FormatterListener;

import java.nio.ByteBuffer;

import static com.mkkl.hantekgui.AppConstants.sampleBatchSize;

public class HantekFormatter extends AbstractByteBufferFormatter {
    private final ADCDataFormatter adcDataFormatter;

    public HantekFormatter(ADCDataFormatter adcDataFormatter, FormatterListener formatterListener) {
        super(formatterListener);
        this.adcDataFormatter = adcDataFormatter;
    }

    int samplesRead = 0;
    float[] ch1data = new float[sampleBatchSize];
    float[] ch2data = new float[sampleBatchSize];

    @Override
    public void formatNext(ByteBuffer buffer) {

        while(buffer.remaining()>=2) {
            float[] samplefloat = adcDataFormatter.formatOneRawSample(new byte[] {buffer.get(), buffer.get()});
            ch1data[samplesRead] = samplefloat[0];
            ch2data[samplesRead] = samplefloat[1];
            samplesRead++;
            if (samplesRead < sampleBatchSize) {
                formatterListener.onFormatted(new SampleBatch(ch1data, ch2data));

                samplesRead = 0;
                ch1data = new float[sampleBatchSize];
                ch2data = new float[sampleBatchSize];
            }
        }
    }
}
