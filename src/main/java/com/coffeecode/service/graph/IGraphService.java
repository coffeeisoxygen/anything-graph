package com.coffeecode.service.graph;

import com.coffeecode.event.listener.GraphEventListener;
import com.coffeecode.model.LocationNode;

public interface IGraphService {

    void addNode(LocationNode node);

    void removeNode(LocationNode node);

    void addEdge(LocationNode source, LocationNode target, double weight);

    void removeEdge(LocationNode source, LocationNode target);

    LocationNode getNode(String id);

    boolean hasNode(LocationNode node);

    boolean hasEdge(LocationNode source, LocationNode target);


}
