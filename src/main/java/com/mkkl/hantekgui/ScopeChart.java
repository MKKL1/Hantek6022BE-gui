package com.mkkl.hantekgui;

import de.gsi.chart.XYChart;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.renderer.ErrorStyle;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.dataset.DataSet;
import de.gsi.dataset.testdata.spi.CosineFunction;

import java.util.Arrays;

public class ScopeChart extends XYChart {
    public ScopeChart(){
        super(new DefaultNumericAxis(), new DefaultNumericAxis());
        getPlugins().add(new Zoomer());
        ErrorDataSetRenderer errorDataSetRenderer = new ErrorDataSetRenderer();
        getRenderers().setAll(errorDataSetRenderer);
        errorDataSetRenderer.setErrorType(ErrorStyle.NONE);
        errorDataSetRenderer.setDrawMarker(false);
        errorDataSetRenderer.setPointReduction(false);
        //renderer1.getDatasets().addAll(new CosineFunction("cos", 500));
    }
}
