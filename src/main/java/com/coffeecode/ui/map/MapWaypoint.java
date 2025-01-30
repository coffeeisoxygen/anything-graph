package com.coffeecode.ui.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointRenderer;

import lombok.Getter;

@Getter
public class MapWaypoint implements Waypoint {

    private final GeoPosition position;
    private final String label;
    private Color color = Color.RED;

    public MapWaypoint(GeoPosition position, String label) {
        this.position = position;
        this.label = label;
    }

    public static class MapWaypointRenderer implements WaypointRenderer<MapWaypoint> {

        @Override
        public void paintWaypoint(Graphics2D g, JXMapViewer map,
                MapWaypoint waypoint) {
            Point point = ((Point) map.getTileFactory().geoToPixel(
                                waypoint.getPosition(), map.getZoom())).getLocation();

            g.setColor(waypoint.color);
            g.fillOval(point.x - 5, point.y - 5, 10, 10);
            g.setColor(Color.BLACK);
            g.drawString(waypoint.label, point.x + 10, point.y + 5);
        }
    }
}
