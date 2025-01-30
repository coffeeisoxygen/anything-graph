package com.coffeecode.service.impl;

import com.coffeecode.model.Location;
import com.coffeecode.service.LocationOperations;
import com.coffeecode.validation.LocationValidator;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
public class LocationService implements LocationOperations {

    private final Map<Long, Location> locations;
    private final LocationValidator validator;

    public LocationService() {
        this.locations = new HashMap<>();
        this.validator = new LocationValidator();
    }

    @Override
    public void addLocation(Location location) {
        try {
            validator.validateLocation(location, locations.values());
            locations.put(location.getId(), location);
            log.info("Added location: {}", location);
        } catch (IllegalArgumentException e) {
            log.error("Failed to add location: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Location> findById(Long id) {
        return Optional.ofNullable(locations.get(id));
    }

    @Override
    public Optional<Location> findByName(String name) {
        return locations.values().stream()
                .filter(location -> location.getName().equals(name))
                .findFirst();
    }

    @Override
    public Collection<Location> findAll() {
        return locations.values();
    }

    @Override
    public void removeById(Long id) {
        locations.remove(id);
    }

    @Override
    public void removeByName(String name) {
        locations.values().removeIf(location -> location.getName().equals(name));
    }

    @Override
    public void clear() {
        locations.clear();
    }

    @Override
    public Optional<Location> findNearest(Location location) {
        return locations.values().stream()
                .min(Comparator.comparingDouble(l -> l.distanceTo(location)));
    }

    @Override
    public double calculateDistance(Long sourceId, Long targetId) {
        Location source = locations.get(sourceId);
        Location target = locations.get(targetId);
        if (source == null || target == null) {
            throw new IllegalArgumentException("Location not found");
        }
        return source.distanceTo(target);
    }

}
