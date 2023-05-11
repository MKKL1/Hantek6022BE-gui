module com.mkkl.hantekgui.hantekgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires de.gsi.chartfx.chart;
    requires de.gsi.chartfx.dataset;
    requires org.slf4j;
    requires hantekapi;
    requires org.apache.commons.lang3;
    requires org.apache.commons.collections4;

    opens com.mkkl.hantekgui to javafx.fxml;
    exports com.mkkl.hantekgui;
    exports com.mkkl.hantekgui.protocol;
    opens com.mkkl.hantekgui.protocol to javafx.fxml;
    exports com.mkkl.hantekgui.capture;
    opens com.mkkl.hantekgui.capture to javafx.fxml;
    exports com.mkkl.hantekgui.ui.chart;
    opens com.mkkl.hantekgui.ui.chart to javafx.fxml;
    exports com.mkkl.hantekgui.settings;
    opens com.mkkl.hantekgui.settings to javafx.fxml;
    exports com.mkkl.hantekgui.ui.chart.render;
    opens com.mkkl.hantekgui.ui.chart.render to javafx.fxml;
    exports com.mkkl.hantekgui.protocol.hantek;
    opens com.mkkl.hantekgui.protocol.hantek to javafx.fxml;
}