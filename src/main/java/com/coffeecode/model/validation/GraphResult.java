package com.coffeecode.model.validation;

import lombok.Value;

@Value
public class GraphResult<T> {

    boolean success;
    String message;
    T data;

    public static <T> GraphResult<T> success(T data) {
        return new GraphResult<>(true, "Operation successful", data);
    }

    public static <T> GraphResult<T> failure(String message) {
        return new GraphResult<>(false, message, null);
    }
}
