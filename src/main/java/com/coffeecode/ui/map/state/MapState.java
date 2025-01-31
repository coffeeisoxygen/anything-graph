package com.coffeecode.ui.map.state;

import org.jxmapviewer.viewer.GeoPosition;

import com.coffeecode.model.LocationNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapState {

    private LocationNode selectedStartNode;
    private LocationNode selectedEndNode;
    private LocationNode sourceNode;  // For edge creation
    private GeoPosition currentPosition;
}
