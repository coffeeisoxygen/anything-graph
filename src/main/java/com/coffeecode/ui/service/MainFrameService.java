package com.coffeecode.ui.service;

import java.util.concurrent.atomic.AtomicBoolean;

import com.coffeecode.model.LocationGraph;

import lombok.Getter;

@Getter
public class MainFrameService {

    private final LocationGraph locationGraph;
    private final AtomicBoolean isRunning;

    public MainFrameService() {
        this.locationGraph = new LocationGraph();
        this.isRunning = new AtomicBoolean(false);
    }

    public void startAlgorithm(String algorithm) {
        if (isRunning.compareAndSet(false, true)) {
            // Algorithm execution logic
        }
    }

    public void stopAlgorithm() {
        isRunning.set(false);
    }

    public void setAnimationSpeed(int speed) {
        // Update animation speed
    }
}
