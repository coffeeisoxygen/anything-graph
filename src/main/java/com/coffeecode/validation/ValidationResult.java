package com.coffeecode.validation;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ValidationResult {

    private final boolean valid;
    private final List<String> errors;

    public static ValidationResult success() {
        return ValidationResult.builder()
                .valid(true)
                .build();
    }

    public static ValidationResult failure(List<String> errors) {
        return ValidationResult.builder()
                .valid(false)
                .errors(errors)
                .build();
    }

    public static ValidationResult failure(String error) {
        return failure(List.of(error));
    }
}
