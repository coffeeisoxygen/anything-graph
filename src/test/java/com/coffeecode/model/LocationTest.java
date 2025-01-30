package com.coffeecode.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class LocationTest {

    @BeforeEach
    void setUp() {
        Location.resetIdGenerator();
    }

    @Test
    void shouldCreateLocationWithValidData() {
        Location location = Location.builder()
                .name("Test Location")
                .latitude(10.0)
                .longitude(20.0)
                .build();

        assertEquals("Test Location", location.getName());
        assertEquals(10.0, location.getLatitude());
        assertEquals(20.0, location.getLongitude());
        assertEquals(1L, location.getId());
    }

    @Test
    void shouldCalculateDistanceCorrectly() {
        Location jakarta = Location.builder()
                .name("Jakarta")
                .latitude(-6.2088)
                .longitude(106.8456)
                .build();

        Location surabaya = Location.builder()
                .name("Surabaya")
                .latitude(-7.2575)
                .longitude(112.7521)
                .build();

        double distance = jakarta.distanceTo(surabaya);
        assertEquals(662.6, distance, 1.0); // Allow 1km margin of error
    }

    @Test
    void shouldIdentifySameLocation() {
        Location loc1 = Location.builder()
                .name("Location 1")
                .latitude(10.0)
                .longitude(20.0)
                .build();

        Location loc2 = Location.builder()
                .name("Location 2")
                .latitude(10.0001)
                .longitude(20.0001)
                .build();

        assertTrue(loc1.isSameLocation(loc2));
    }

    @Test
    void shouldGenerateCorrectDisplayName() {
        Location location = Location.builder()
                .name("Test")
                .latitude(10.5)
                .longitude(20.5)
                .build();

        assertEquals("Test (10.50, 20.50)", location.getDisplayName());
    }
}
