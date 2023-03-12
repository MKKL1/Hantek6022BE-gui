package com.mkkl.hantekgui;

import com.mkkl.hantekgui.protocol.OscilloscopeChannel;
import de.gsi.chart.XYChart;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.renderer.ErrorStyle;
import de.gsi.chart.renderer.datareduction.DefaultDataReducer;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.dataset.DataSet;
import de.gsi.dataset.spi.FloatDataSet;
import de.gsi.dataset.testdata.spi.CosineFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScopeChart extends XYChart {
    public static int MIN_PIXEL_DISTANCE = 2;
    private FloatDataSet[] channelDataSets;
    public ScopeChart() {
        super(new DefaultNumericAxis(), new DefaultNumericAxis());
        getAxes().forEach(axis -> axis.setAnimated(false));
        getPlugins().add(new Zoomer());
        ErrorDataSetRenderer errorDataSetRenderer = new ErrorDataSetRenderer();
        errorDataSetRenderer.setErrorType(ErrorStyle.NONE);
        errorDataSetRenderer.setDrawMarker(false);
        errorDataSetRenderer.setDashSize(MIN_PIXEL_DISTANCE);
        errorDataSetRenderer.setPointReduction(true);

        final DefaultDataReducer reductionAlgorithm = (DefaultDataReducer) errorDataSetRenderer.getRendererDataReducer();
        reductionAlgorithm.setMinPointPixelDistance(MIN_PIXEL_DISTANCE);

        getRenderers().setAll(errorDataSetRenderer);
        //renderer1.getDatasets().addAll(new CosineFunction("cos", 500));
    }

    public void setChannels(List<OscilloscopeChannel> channels) {
        channelDataSets = new FloatDataSet[channels.size()];
        for(OscilloscopeChannel channel : channels) {
            channelDataSets[channel.id()] = new FloatDataSet(channel.name());
        }
    }

    public FloatDataSet getDataSetByChannelId(int channelid) {
        return channelDataSets[channelid];
    }

    public
}
