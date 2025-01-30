package com.coffeecode.validation;

public interface Validator<T> {

    ValidationResult validate(T item);

    ValidationResult validateNew(T item, Iterable<T> existing);
}
