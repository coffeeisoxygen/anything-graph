package com.coffeecode.ui.map.builder;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapViewerBuilder {

    private static final GeoPosition DEFAULT_LOCATION
            = new GeoPosition(-6.2088, 106.8456); // Jakarta
    private static final int DEFAULT_ZOOM = 7;

    private GeoPosition location = DEFAULT_LOCATION;
    private int zoomLevel = DEFAULT_ZOOM;

    public static MapViewerBuilder create() {
        return new MapViewerBuilder();
    }

    public MapViewerBuilder withLocation(GeoPosition location) {
        this.location = location;
        return this;
    }

    public MapViewerBuilder withZoom(int zoom) {
        this.zoomLevel = zoom;
        return this;
    }

    public JXMapViewer build() {
        // Create map viewer
        JXMapViewer mapViewer = new JXMapViewer();

        // Setup tile source
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Configure view
        mapViewer.setAddressLocation(location);
        mapViewer.setZoom(zoomLevel);

        return mapViewer;
    }
}
