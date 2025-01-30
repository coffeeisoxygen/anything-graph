package com.coffeecode.ui.panelmap;

import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import lombok.Getter;

@Getter
public class MapWaypoint implements Waypoint {

    private final GeoPosition position;
    private static final DefaultWaypointRenderer renderer = new DefaultWaypointRenderer();

    public MapWaypoint(GeoPosition position) {
        this.position = position;
    }
}
