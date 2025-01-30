package com.coffeecode.ui.map.exception;

public class MapInitializationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MapInitializationException(String message) {
        super(message);
    }

    public MapInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
