package com.coffeecode.ui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
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
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import com.coffeecode.model.LocationNode;

import lombok.Getter;

@Getter
public class MapPanel extends JPanel {

    private final JXMapViewer mapViewer;
    private final List<LocationNode> nodes;
    private final Set<WaypointPainter<Waypoint>> painters;
    private final JPopupMenu popupMenu;
    private final JTextField locationNameField;

    // Add event listener support
    private final List<NodeChangeListener> nodeChangeListeners = new ArrayList<>();

    public MapPanel() {
        setLayout(new BorderLayout());
        nodes = new ArrayList<>();
        painters = new HashSet<>();

        // Initialize map viewer
        mapViewer = new JXMapViewer();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Set default position (center of map)
        GeoPosition jakarta = new GeoPosition(-6.2088, 106.8456);
        mapViewer.setAddressLocation(jakarta);
        mapViewer.setZoom(7);

        // Add interactions
        setupMapInteraction();

        // Create popup menu
        popupMenu = new JPopupMenu();
        locationNameField = new JTextField(20);
        setupPopupMenu();

        // Add components
        add(mapViewer, BorderLayout.CENTER);
    }

    private void setupMapInteraction() {
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        // Add right-click handler for node placement
        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Point p = e.getPoint();
                    GeoPosition geo = mapViewer.convertPointToGeoPosition(p);

                    // Update location name field asynchronously
                    new SwingWorker<String, Void>() {
                        @Override
                        protected String doInBackground() {
                            return String.format("%.4f, %.4f", geo.getLatitude(), geo.getLongitude());
                        }

                        @Override
                        protected void done() {
                            try {
                                locationNameField.setText(get());
                            } catch (Exception ex) {
                                locationNameField.setText("Error fetching location name");
                            }
                        }
                    }.execute();

                    popupMenu.show(mapViewer, p.x, p.y);
                }
            }
        });
    }

    private void setupPopupMenu() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Location: "), BorderLayout.WEST);
        panel.add(locationNameField, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Node");
        addButton.addActionListener(e -> {
            Point p = popupMenu.getLocation();
            GeoPosition geo = mapViewer.convertPointToGeoPosition(p);
            addNode(new LocationNode(locationNameField.getText(), geo.getLatitude(), geo.getLongitude()));
            popupMenu.setVisible(false);
        });

        panel.add(addButton, BorderLayout.EAST);
        popupMenu.add(panel);
    }

    public void addNodeChangeListener(NodeChangeListener listener) {
        nodeChangeListeners.add(listener);
    }

    public void removeNodeChangeListener(NodeChangeListener listener) {
        nodeChangeListeners.remove(listener);
    }

    private void notifyNodeAdded(LocationNode node) {
        for (NodeChangeListener listener : nodeChangeListeners) {
            listener.onNodeAdded(node);
        }
    }

    public void addNode(LocationNode node) {
        nodes.add(node);
        updateWaypoints();
        notifyNodeAdded(node);
    }

    private void updateWaypoints() {
        Set<Waypoint> waypoints = new HashSet<>();
        for (LocationNode node : nodes) {
            waypoints.add(new DefaultWaypoint(
                    new GeoPosition(node.getLatitude(), node.getLongitude())));
        }

        WaypointPainter<Waypoint> painter = new WaypointPainter<>();
        painter.setWaypoints(waypoints);
        mapViewer.setOverlayPainter(painter);
        mapViewer.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Map Panel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new MapPanel());
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
