package com.coffeecode.ui.panelgraph;

public class GraphStylesheet {

    private GraphStylesheet() {
    }

    // Node states
    public static final String STATE_DEFAULT = "default";
    public static final String STATE_VISITED = "visited";
    public static final String STATE_CURRENT = "current";
    public static final String STATE_PATH = "path";

    // Style constants
    private static final String NODE_SIZE = "30px";
    private static final String NODE_COLOR = "#666666";
    private static final String NODE_VISITED_COLOR = "#00ff00";
    private static final String NODE_CURRENT_COLOR = "#ff0000";
    private static final String NODE_PATH_COLOR = "#0000ff";
    private static final String EDGE_COLOR = "#999999";
    private static final String EDGE_VISITED_COLOR = "#00ff00";

    public static String getDefaultStylesheet() {
        return """
            node {
                size: %s;
                fill-color: %s;
                text-size: 20px;
                text-color: white;
                text-style: bold;
                text-alignment: center;
            }
            node.visited {
                fill-color: %s;
            }
            node.current {
                fill-color: %s;
            }
            node.path {
                fill-color: %s;
            }
            edge {
                size: 2px;
                fill-color: %s;
                text-size: 20px;
            }
            edge.visited {
                fill-color: %s;
                size: 3px;
            }
            """.formatted(
                NODE_SIZE,
                NODE_COLOR,
                NODE_VISITED_COLOR,
                NODE_CURRENT_COLOR,
                NODE_PATH_COLOR,
                EDGE_COLOR,
                EDGE_VISITED_COLOR
        );
    }
}
