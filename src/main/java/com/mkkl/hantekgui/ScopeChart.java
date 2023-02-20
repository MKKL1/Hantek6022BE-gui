package com.mkkl.hantekgui;

import de.gsi.chart.XYChart;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.renderer.ErrorStyle;
import de.gsi.chart.renderer.datareduction.DefaultDataReducer;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.dataset.DataSet;
import de.gsi.dataset.testdata.spi.CosineFunction;

import java.util.Arrays;

public class ScopeChart extends XYChart {
    public static int MIN_PIXEL_DISTANCE = 2;

    public ScopeChart(){
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
}
