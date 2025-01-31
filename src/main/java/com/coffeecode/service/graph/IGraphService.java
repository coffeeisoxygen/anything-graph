package com.coffeecode.service.graph;

import com.coffeecode.model.LocationNode;
import com.coffeecode.event.listener.GraphEventListener;

public interface IGraphService {

    void addNode(LocationNode node);

    void removeNode(LocationNode node);

    void addEdge(LocationNode source, LocationNode target, double weight);

    void removeEdge(LocationNode source, LocationNode target);

    LocationNode getNode(String id);

    boolean hasNode(LocationNode node);

    boolean hasEdge(LocationNode source, LocationNode target);

    void addListener(GraphEventListener listener);

    void removeListener(GraphEventListener listener);
}
