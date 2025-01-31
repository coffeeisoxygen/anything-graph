package com.coffeecode.model.validation;

import java.util.Set;

import com.coffeecode.model.LocationNode;

public class LocationValidator {

    private static final double COORDINATE_EPSILON = 0.000001;

    public static void validateNewNode(LocationNode newNode, Set<LocationNode> existingNodes) {
        validateId(newNode.getId(), existingNodes);
        validateCoordinates(newNode.getLatitude(), newNode.getLongitude(), existingNodes);
    }

    private static void validateId(String id, Set<LocationNode> existingNodes) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        if (existingNodes.stream().anyMatch(node -> node.getId().equals(id))) {
            throw new IllegalArgumentException("Node with ID '" + id + "' already exists");
        }
    }

    private static void validateCoordinates(double lat, double lon, Set<LocationNode> existingNodes) {
        if (lat < -90 || lat > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (lon < -180 || lon > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }

        boolean locationExists = existingNodes.stream().anyMatch(node
                -> Math.abs(node.getLatitude() - lat) < COORDINATE_EPSILON
                && Math.abs(node.getLongitude() - lon) < COORDINATE_EPSILON
        );

        if (locationExists) {
            throw new IllegalArgumentException(
                    String.format("Location already exists at (%.6f, %.6f)", lat, lon)
            );
        }
    }

    // prevent instatiation
    private LocationValidator() {
        throw new IllegalStateException("Utility class");
    }
}
