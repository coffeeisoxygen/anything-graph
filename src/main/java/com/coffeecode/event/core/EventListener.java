package com.coffeecode.event.core;

@FunctionalInterface
public interface EventListener {

    void onEvent(Object event);
}
