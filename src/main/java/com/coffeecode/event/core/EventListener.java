package com.coffeecode.event.core;

import java.util.function.Consumer;

@FunctionalInterface
public interface EventListener<T> extends Consumer<T> {

    void onEvent(T event);

    @Override
    default void accept(T event) {
        onEvent(event);
    }
}
