package com.coffeecode.ui.map;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.jxmapviewer.viewer.GeoPosition;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapPanelEvent {

    private final MapPanel panel;
    private final MapPanelService service;

    public MapPanelEvent(MapPanel panel, MapPanelService service) {
        this.panel = panel;
        this.service = service;
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
                    panel.showAddNodeDialog(get(), geo, p);
                } catch (Exception ex) {
                    log.error("Error fetching location name", ex);
                }
            }
        }.execute();
    }
}
