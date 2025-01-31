package com.coffeecode.util;

import com.coffeecode.model.LocationNode;

import jakarta.validation.constraints.NotNull;

public final class HaversineDistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculates the Haversine distance between two LocationNodes.
     *
     * @param source The source LocationNode
     * @param destination The destination LocationNode
     * @return Distance in kilometers
     * @throws IllegalArgumentException if inputs are null
     */
    public static double calculateDistance(@NotNull LocationNode source,
            @NotNull LocationNode destination) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }

        double lat1 = Math.toRadians(source.getLatitude());
        double lon1 = Math.toRadians(source.getLongitude());
        double lat2 = Math.toRadians(destination.getLatitude());
        double lon2 = Math.toRadians(destination.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Validates if coordinates are within valid ranges
     */
    public static boolean isValidCoordinate(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90
                && longitude >= -180 && longitude <= 180;
    }

    private HaversineDistanceCalculator() {
        throw new IllegalStateException("Utility class");
    }
}
