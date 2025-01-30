package com.coffeecode.ui;

import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import lombok.Getter;

@Getter
public class DefaultWaypoint implements Waypoint {
    private final GeoPosition position;
    private static final DefaultWaypointRenderer renderer = new DefaultWaypointRenderer();

    public DefaultWaypoint(GeoPosition position) {
        this.position = position;
    }
}
