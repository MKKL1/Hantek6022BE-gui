package com.mkkl.hantekgui;

import de.gsi.chart.XYChart;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.dataset.DataSet;
import de.gsi.dataset.testdata.spi.CosineFunction;

public class ScopeChart extends XYChart {
    final ErrorDataSetRenderer renderer1 = new ErrorDataSetRenderer();
    public ScopeChart(){
        super(new DefaultNumericAxis(), new DefaultNumericAxis());
        getPlugins().add(new Zoomer());
        getRenderers().add(renderer1);
        //renderer1.getDatasets().addAll(new CosineFunction("cos", 500));
    }

    public void draw(DataSet dataSet) {
        renderer1.getDatasets().add(dataSet);
    }
}
