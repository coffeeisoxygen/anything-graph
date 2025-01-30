package com.coffeecode.model;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class PointNodeTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testLocationValid() {
        PointNode location = PointNode.builder()
                .id(1L)
                .name("Test Location")
                .longitude(50.0)
                .latitude(20.0)
                .build();

        Set<ConstraintViolation<PointNode>> violations = validator.validate(location);
        assertThat(violations).isEmpty();
    }

    @Test
    void testLocationInvalidId() {
        PointNode location = PointNode.builder()
                .name("Test Location")
                .longitude(50.0)
                .latitude(20.0)
                .build();

        Set<ConstraintViolation<PointNode>> violations = validator.validate(location);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("ID cannot be null");
    }

    @Test
    void testLocationInvalidName() {
        PointNode location = PointNode.builder()
                .id(1L)
                .name("") // Invalid: empty name
                .longitude(50.0)
                .latitude(20.0)
                .build();

        Set<ConstraintViolation<PointNode>> violations = validator.validate(location);
        assertThat(violations).hasSize(2); // Will have 2 violations: NotBlank and Size
        assertThat(violations).extracting("message")
                .containsOnly(
                        "Location name cannot be blank",
                        "Name must be between 2 and 50 characters"
                );
    }

    @Test
    void testLocationInvalidCoordinates() {
        PointNode location = PointNode.builder()
                .id(1L)
                .name("Test Location")
                .longitude(181.0) // Invalid: > 180
                .latitude(91.0) // Invalid: > 90
                .build();

        Set<ConstraintViolation<PointNode>> violations = validator.validate(location);
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
                .containsOnly(
                        "Longitude must be less than 180",
                        "Latitude must be less than 90"
                );
    }

    @Test
    void testLocationBoundaryValues() {
        PointNode location = PointNode.builder()
                .id(1L)
                .name("Test")
                .longitude(-180.0) // Valid boundary
                .latitude(-90.0) // Valid boundary
                .build();

        Set<ConstraintViolation<PointNode>> violations = validator.validate(location);
        assertThat(violations).isEmpty();
    }

    @Test
    void testEqualsAndHashCode() {
        PointNode location1 = PointNode.builder()
                .id(1L)
                .name("Location 1")
                .longitude(50.0)
                .latitude(20.0)
                .build();

        PointNode location2 = PointNode.builder()
                .id(1L)
                .name("Location 2")
                .longitude(60.0)
                .latitude(30.0)
                .build();

        assertThat(location1).isEqualTo(location2);
        assertThat(location1.hashCode()).isEqualTo(location2.hashCode());
    }

    @Test
    void testGetDisplayName() {
        PointNode location = PointNode.builder()
                .id(1L)
                .name("Test Location")
                .longitude(50.0)
                .latitude(20.0)
                .build();

        String displayName = location.getDisplayName();
        assertThat(displayName).isEqualTo("Test Location (20.00, 50.00)");
    }
}
