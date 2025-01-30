package com.coffeecode.validation;

import com.coffeecode.model.ILocation;
import com.coffeecode.model.Location;
import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
public class LocationValidator implements Validator<Location> {

    private static final jakarta.validation.Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Override
    public ValidationResult validate(Location location) {
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        if (!violations.isEmpty()) {
            return ValidationResult.failure(
                    violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .toList()
            );
        }
        return ValidationResult.success();
    }

    @Override
    public ValidationResult validateNew(Location newLocation, Iterable<Location> existing) {
        ValidationResult basicValidation = validate(newLocation);
        if (!basicValidation.isValid()) {
            return basicValidation;
        }

        for (ILocation existingLocation : existing) {
            if (existingLocation.isSameLocation(newLocation)) {
                return ValidationResult.failure("Location with these coordinates already exists");
            }
        }

        return ValidationResult.success();
    }
}
