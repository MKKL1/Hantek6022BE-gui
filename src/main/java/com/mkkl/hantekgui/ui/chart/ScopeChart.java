package com.mkkl.hantekgui.ui.chart;

import com.mkkl.hantekgui.protocol.OscilloscopeChannel;
import de.gsi.chart.XYChart;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.plugins.ParameterMeasurements;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.renderer.ErrorStyle;
import de.gsi.chart.renderer.datareduction.DefaultDataReducer;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.dataset.spi.FloatDataSet;

import java.util.List;

public class ScopeChart extends XYChart {
    public static int MIN_PIXEL_DISTANCE = 3;
    private FloatDataSet[] channelDataSets;
    ErrorDataSetRenderer errorDataSetRenderer = new ErrorDataSetRenderer();
    public ScopeChart() {
        super(new DefaultNumericAxis(), new DefaultNumericAxis());
        getAxes().forEach(axis -> axis.setAnimated(false));
        getPlugins().add(new Zoomer());
        getPlugins().add(new ParameterMeasurements());

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
        errorDataSetRenderer.getDatasets().addAll(channelDataSets);
    }

    public FloatDataSet getDataSetByChannelId(int channelid) {
        return channelDataSets[channelid];
    }
}
