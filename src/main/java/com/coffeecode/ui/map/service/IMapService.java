package com.coffeecode.ui.map.service;

import org.jxmapviewer.JXMapViewer;

import com.coffeecode.listener.NodeChangeListener;
import com.coffeecode.model.LocationNode;

public interface IMapService {

    void addNode(LocationNode node);

    void removeNode(LocationNode node);

    void updateStartNode(LocationNode node);

    void updateEndNode(LocationNode node);

    void updateWaypoints(JXMapViewer mapViewer);

    void addListener(NodeChangeListener listener);
}
