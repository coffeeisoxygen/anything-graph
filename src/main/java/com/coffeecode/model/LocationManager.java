package com.coffeecode.model;

import java.util.*;

public class LocationManager {

    private final Map<Long, Location> locationMap;

    public LocationManager() {
        this.locationMap = new HashMap<>();
    }

    public void addLocation(Location location) {
        locationMap.put(location.getId(), location);
    }

    public Location getLocationById(Long id) {
        return locationMap.get(id);
    }

    public void removeLocation(Long id) {
        locationMap.remove(id);
    }

    public Collection<Location> getAllLocations() {
        return locationMap.values();
    }

    public Location findNearestLocation(Location location) {
        return locationMap.values().stream()
                .min(Comparator.comparingDouble(l -> l.distanceTo(location)))
                .orElse(null); // Return null if no locations are found
    }

    public void updateLocation(Long id, Location newLocation) {
        locationMap.computeIfPresent(id, (key, oldLocation) -> newLocation);
    }
}
