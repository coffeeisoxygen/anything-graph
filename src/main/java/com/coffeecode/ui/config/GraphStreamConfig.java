package com.coffeecode.ui.config;

import org.graphstream.graph.Graph;

public class GraphStreamConfig {

    private static final String DEFAULT_STYLE
            = "node {"
            + "   size: 30px;"
            + "   fill-color: #666666;"
            + "   text-size: 14;"
            + "}"
            + "node.visited {"
            + "   fill-color: #00ff00;"
            + "}"
            + "edge {"
            + "edge {"
            + "   size: 2px;"
            + "   fill-color: #666666;"
            + "}";

    public static void configureGraph(Graph graph) {
        System.setProperty("org.graphstream.ui", "swing");
        graph.setAttribute("ui.stylesheet", DEFAULT_STYLE);
        graph.setAttribute("ui.quality", true);
        graph.setAttribute("ui.antialias", true);
    }

    // prevent init
    private GraphStreamConfig (){
    }
}
