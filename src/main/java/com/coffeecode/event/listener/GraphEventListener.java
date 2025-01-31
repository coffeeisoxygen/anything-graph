package com.coffeecode.event.listener;

import com.coffeecode.event.core.GraphEvent;

@FunctionalInterface
public interface GraphEventListener {

    void onGraphEvent(GraphEvent event);
}
