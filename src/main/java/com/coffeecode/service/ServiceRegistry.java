package com.coffeecode.service;

import com.coffeecode.event.manager.EventManager;
import com.coffeecode.event.manager.VisualizationManager;
import com.coffeecode.service.algorithm.AlgorithmService;
import com.coffeecode.service.graph.GraphService;
import lombok.Getter;

@Getter
public class ServiceRegistry {

    private final EventManager eventManager;
    private final VisualizationManager visualizationManager;
    private final GraphService graphService;
    private final AlgorithmService algorithmService;

    public ServiceRegistry() {
        this.eventManager = new EventManager();
        this.visualizationManager = new VisualizationManager(eventManager);
        this.graphService = new GraphService(eventManager);
        this.algorithmService = new AlgorithmService(eventManager, visualizationManager);
    }
}
