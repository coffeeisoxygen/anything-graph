package com.coffeecode.ui.map;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jxmapviewer.viewer.GeoPosition;

import com.coffeecode.ui.map.service.IMapService;
import com.coffeecode.ui.map.service.NominatimService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapPanelEvent {

    private final MapPanel panel;
    private final IMapService mapService;

    public MapPanelEvent(MapPanel panel, IMapService mapService) {
        this.panel = panel;
        this.mapService = mapService;
    }

    public MouseAdapter createNodePlacementListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    handleRightClick(e.getPoint());
                }
            }
        };
    }

    private void handleRightClick(Point p) {
        GeoPosition geo = panel.getMapViewer().convertPointToGeoPosition(p);
        fetchLocationName(geo, p);
    }

    private void fetchLocationName(GeoPosition geo, Point p) {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return NominatimService.getLocationName(
                        geo.getLatitude(),
                        geo.getLongitude()
                );
            }

            @Override
            protected void done() {
                try {
                    String locationName = get();
                    SwingUtilities.invokeLater(() -> {
                        // Update location field first
                        panel.getPopupMenu().getLocationField().setText(locationName);
                        // Then show popup at correct position
                        panel.getPopupMenu().show(panel.getMapViewer(), p.x, p.y);
                    });
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.error("Thread was interrupted", ex);
                } catch (ExecutionException ex) {
                    log.error("Error fetching location name", ex);
                }
                // Show popup with coordinates as fallback
                String fallback = String.format("%.4f, %.4f",
                        geo.getLatitude(),
                        geo.getLongitude()
                );
                panel.getPopupMenu().getLocationField().setText(fallback);
                panel.getPopupMenu().show(panel.getMapViewer(), p.x, p.y);
            }
        }.execute();
    }
}
