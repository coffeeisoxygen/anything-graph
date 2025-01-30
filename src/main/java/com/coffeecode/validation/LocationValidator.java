package com.coffeecode.validation;

import com.coffeecode.model.Location;
import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class LocationValidator {

    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Main validation method that handles both basic validation and duplicate
     * check
     */
    public void validateLocation(Location location, Collection<Location> existingLocations) {
        // Basic validation
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Invalid location: " + errors);
        }

        // Duplicate check
        boolean isDuplicate = existingLocations.stream()
                .anyMatch(loc -> loc.isSameLocation(location));
        if (isDuplicate) {
            throw new IllegalArgumentException("Location already exists");
        }
    }
}
