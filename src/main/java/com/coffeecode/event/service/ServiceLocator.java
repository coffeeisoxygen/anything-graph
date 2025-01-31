package com.coffeecode.event.service;

import com.coffeecode.event.core.EventManager;

import lombok.Getter;

@Getter
public class ServiceLocator {

    private static final ServiceLocator INSTANCE = new ServiceLocator();

    private final EventManager eventManager;

    private ServiceLocator() {
        this.eventManager = new EventManager();
    }

    public static ServiceLocator getInstance() {
        return INSTANCE;
    }
}
