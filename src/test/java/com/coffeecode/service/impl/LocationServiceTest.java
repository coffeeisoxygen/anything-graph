package com.coffeecode.service.impl;

import com.coffeecode.exception.LocationException;
import com.coffeecode.model.Location;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class LocationServiceTest {

    private LocationService service;

    @BeforeEach
    void setUp() {
        service = new LocationService();
        Location.resetIdGenerator();
    }

    @Test
    void shouldAddValidLocation() {
        Location location = Location.builder()
                .name("Test Location")
                .latitude(0)
                .longitude(0)
                .build();

        assertDoesNotThrow(() -> service.addLocation(location));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void shouldFindLocationById() {
        Location location = Location.builder()
                .name("Test")
                .latitude(0)
                .longitude(0)
                .build();
        service.addLocation(location);

        assertTrue(service.findById(1L).isPresent());
        assertEquals("Test", service.findById(1L).get().getName());
    }

    @Test
    void shouldFindNearestLocation() {
        Location loc1 = Location.builder()
                .name("Location 1")
                .latitude(0)
                .longitude(0)
                .build();

        Location loc2 = Location.builder()
                .name("Location 2")
                .latitude(1)
                .longitude(1)
                .build();

        service.addLocation(loc1);
        service.addLocation(loc2);

        Location searchPoint = Location.builder()
                .name("Search")
                .latitude(0.1)
                .longitude(0.1)
                .build();

        assertEquals(loc1, service.findNearest(searchPoint).get());
    }

    @Test
    void shouldCalculateDistance() {
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

        service.addLocation(jakarta);
        service.addLocation(surabaya);

        double distance = service.calculateDistance(1L, 2L);
        assertEquals(662.6, distance, 1.0); // Allow 1km margin of error
    }

    @Test
    void shouldThrowExceptionForNonExistentLocations() {
        assertThrows(LocationException.class,
                () -> service.calculateDistance(1L, 2L));
    }
}
