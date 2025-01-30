package com.coffeecode;

import java.util.Optional;

import com.coffeecode.model.Location;
import com.coffeecode.service.impl.LocationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    public static void main(String[] args) {
        log.info("Starting application...");

        // Create a LocationService instance
        LocationService locationService = new LocationService();

        // Add some locations
        Location jakarta = Location.builder()
                .id(1L)
                .name("Jakarta")
                .latitude(-6.2088)
                .longitude(106.8456)
                .build();

        Location surabaya = Location.builder()
                .id(2L)
                .name("Surabaya")
                .latitude(-7.2575)
                .longitude(112.7521)
                .build();

        locationService.addLocation(jakarta);
        locationService.addLocation(surabaya);

        // Find nearest location to a given point
        Location searchPoint = Location.builder()
                .name("Search Point")
                .latitude(-6.5)
                .longitude(107.0)
                .build();

        Optional<Location> nearestLocation = locationService.findNearest(searchPoint);
        nearestLocation.ifPresent(location -> log.info("Nearest location to {} is {}", searchPoint.getName(), location.getName()));

        // Calculate distance between two locations
        double distance = locationService.calculateDistance(1L, 2L);
        log.info("Distance between Jakarta and Surabaya: {} km", distance);
    }
}
