package com.coffeecode.service;

import com.coffeecode.model.Location;
import java.util.Collection;
import java.util.Optional;

public interface LocationOperations {

    void addLocation(Location location);

    Optional<Location> findById(Long id);

    Optional<Location> findByName(String name);

    Collection<Location> findAll();

    void removeById(Long id);

    void removeByName(String name);

    void clear();

    Optional<Location> findNearest(Location location);

    double calculateDistance(Long sourceId, Long targetId);
}
