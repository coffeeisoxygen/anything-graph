package com.coffeecode.validation;

import com.coffeecode.exception.LocationException;
import com.coffeecode.model.Location;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class LocationValidatorTest {

    private LocationValidator validator;
    private Collection<Location> existingLocations;

    @BeforeEach
    void setUp() {
        validator = new LocationValidator();
        existingLocations = new ArrayList<>();
        Location.resetIdGenerator();
    }

    @Test
    void shouldValidateValidLocation() {
        Location location = Location.builder()
                .name("Valid Location")
                .latitude(0)
                .longitude(0)
                .build();

        assertDoesNotThrow(() -> validator.validateLocation(location, existingLocations));
    }

    @Test
    void shouldRejectNullLocation() {
        assertThrows(LocationException.class,
                () -> validator.validateLocation(null, existingLocations));
    }

    @Test
    void shouldRejectInvalidLatitude() {
        Location location = Location.builder()
                .name("Invalid")
                .latitude(91)
                .longitude(0)
                .build();

        assertThrows(LocationException.class,
                () -> validator.validateLocation(location, existingLocations));
    }

    @Test
    void shouldRejectDuplicateLocation() {
        Location existing = Location.builder()
                .name("Existing")
                .latitude(10)
                .longitude(10)
                .build();
        existingLocations.add(existing);

        Location duplicate = Location.builder()
                .name("Duplicate")
                .latitude(10.0001)
                .longitude(10.0001)
                .build();

        assertThrows(LocationException.class,
                () -> validator.validateLocation(duplicate, existingLocations));
    }
}
