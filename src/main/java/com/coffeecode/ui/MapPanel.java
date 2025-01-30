package com.coffeecode.ui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import lombok.Getter;

@Getter
public class MapPanel extends JPanel {

    private final JXMapViewer mapViewer;
    private final Set<DefaultWaypoint> waypoints;
    private final WaypointPainter<DefaultWaypoint> waypointPainter;

    public MapPanel() {
        setLayout(new BorderLayout());

        // Create a map viewer
        mapViewer = new JXMapViewer();
        waypoints = new HashSet<>();
        waypointPainter = new WaypointPainter<>();

        // Setup OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Set default zoom and center position (e.g., London)
        GeoPosition london = new GeoPosition(51.5, -0.12);
        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(london);

        // Add interactions
        setupMapInteraction();

        // Add click listener for adding waypoints
        setupWaypointCreation();

        // Add the map viewer to the panel
        add(new JScrollPane(mapViewer), BorderLayout.CENTER);
    }

    private void setupMapInteraction() {
        // Add Mouse and Key interactions
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));
    }

    private void setupWaypointCreation() {
        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double click to add waypoint
                    Point p = e.getPoint();
                    GeoPosition geo = mapViewer.convertPointToGeoPosition(p);
                    addWaypoint(geo);
                }
            }
        });
    }

    public void addWaypoint(GeoPosition position) {
        DefaultWaypoint waypoint = new DefaultWaypoint(position);
        waypoints.add(waypoint);
        updateWaypoints();
    }

    public void clearWaypoints() {
        waypoints.clear();
        updateWaypoints();
    }

    private void updateWaypoints() {
        waypointPainter.setWaypoints(waypoints);
        mapViewer.setOverlayPainter(waypointPainter);
        mapViewer.repaint();
    }

    public Set<GeoPosition> getWaypointPositions() {
        Set<GeoPosition> positions = new HashSet<>();
        for (DefaultWaypoint waypoint : waypoints) {
            positions.add(waypoint.getPosition());
        }
        return positions;
    }
}
