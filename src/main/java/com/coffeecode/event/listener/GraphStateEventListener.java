package com.coffeecode.event.listener;

import com.coffeecode.event.core.GraphStateEvent;

@FunctionalInterface
public interface GraphStateEventListener {

    void onGraphStateEvent(GraphStateEvent event);
}
