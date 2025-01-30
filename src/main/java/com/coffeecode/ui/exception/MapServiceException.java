package com.coffeecode.ui.exception;

public class MapServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MapServiceException(String message) {
        super(message);
    }

    public MapServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
