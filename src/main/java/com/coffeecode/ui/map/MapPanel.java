package com.coffeecode.ui.map;

import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.GeoPosition;

import com.coffeecode.event.NodeChangeListener;
import com.coffeecode.ui.exception.MapInitializationException;
import com.coffeecode.ui.map.builder.MapViewerBuilder;
import com.coffeecode.ui.map.component.NodePopupMenu;
import com.coffeecode.ui.map.service.IMapService;
import com.coffeecode.ui.map.service.MapService;
import com.coffeecode.ui.map.state.MapState;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MapPanel extends JPanel {

    // Core components
    private final JXMapViewer mapViewer;
    private final MapState state;
    private final IMapService mapService;
    private NodePopupMenu popupMenu;
    private MapPanelEvent eventHandler;

    public MapPanel() {
        this(new MapService()); // Provide default implementation
    }

    public MapPanel(IMapService mapService) {
        this.mapService = mapService;
        this.state = new MapState();
        this.mapViewer = createMapViewer();

        initializeComponents();
        setupEventHandlers();

        log.info("MapPanel initialized");
    }

    private JXMapViewer createMapViewer() {
        return MapViewerBuilder.create()
                .withLocation(new GeoPosition(-6.2088, 106.8456))
                .withZoom(7)
                .build();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Initialize components
        popupMenu = new NodePopupMenu();
        eventHandler = new MapPanelEvent(this, mapService);

        // Setup UI
        add(mapViewer, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        try {
            setupMapControls();
            setupNodePlacement();
        } catch (Exception e) {
            log.error("Failed to setup event handlers", e);
            throw new MapInitializationException("Failed to initialize map panel", e);
        }
    }

    private void setupMapControls() {
        // Basic map controls
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));
    }

    private void setupNodePlacement() {
        // Custom node placement listener
        mapViewer.addMouseListener(eventHandler.createNodePlacementListener());
    }

    public void showPopup(GeoPosition position, Point point) {
        state.setCurrentPosition(position);
        popupMenu.show(mapViewer, point.x, point.y);
    }

    public void addNodeChangeListener(NodeChangeListener listener) {
        mapService.addListener(listener);
    }

    public void updateWaypoints() {
        mapService.updateWaypoints(mapViewer);
    }
}
