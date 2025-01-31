package com.coffeecode.event;

@FunctionalInterface
public interface GraphEventListener {

    void onGraphEvent(GraphEvent event);
}
