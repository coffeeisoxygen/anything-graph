package com.coffeecode;

import com.coffeecode.model.Location;
import com.coffeecode.model.LocationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    public static void main(String[] args) {
        log.info("Starting application...");

        // Sample usage of LocationManager
        LocationManager locationManager = new LocationManager();

        // Adding locations
        Location location1 = Location.builder().name("Location 1").longitude(34.05).latitude(-118.25).build();
        Location location2 = Location.builder().name("Location 2").longitude(40.71).latitude(-74.01).build();
        locationManager.addLocation(location1);
        locationManager.addLocation(location2);

        // Printing all locations
        locationManager.getAllLocations().forEach(location -> log.info(location.toString()));

        // Finding nearest location to a given point
        Location targetLocation = Location.builder().name("Target Location").longitude(36.16).latitude(-115.15).build();
        Location nearestLocation = locationManager.findNearestLocation(targetLocation);
        log.info("Nearest location to {} is {}", targetLocation.getName(), nearestLocation.getName());

        // Calculating distances
        double distance1 = targetLocation.distanceTo(location1);
        double distance2 = targetLocation.distanceTo(location2);
        log.info("Distance from {} to {} is {} km", targetLocation.getName(), location1.getName(), distance1);
        log.info("Distance from {} to {} is {} km", targetLocation.getName(), location2.getName(), distance2);

    }
}
