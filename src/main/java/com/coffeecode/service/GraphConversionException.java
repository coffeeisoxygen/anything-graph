package com.coffeecode.service;

public class GraphConversionException extends RuntimeException {

    public GraphConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphConversionException(String message) {
        super(message);
    }   

}
