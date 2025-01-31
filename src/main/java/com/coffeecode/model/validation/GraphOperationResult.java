package com.coffeecode.model.validation;

import lombok.Value;

@Value
public class GraphOperationResult<T> {

    boolean success;
    String message;
    T data;

    public static <T> GraphOperationResult<T> success(T data) {
        return new GraphOperationResult<>(true, "Operation successful", data);
    }

    public static <T> GraphOperationResult<T> failure(String message) {
        return new GraphOperationResult<>(false, message, null);
    }
}
