package com.coffeecode.model.weight;

import com.coffeecode.util.HaversineDistanceCalculator;

public final class WeightStrategies {

    public static final EdgeWeightStrategy HAVERSINE_DISTANCE
            = HaversineDistanceCalculator::calculateDistance;

    public static final EdgeWeightStrategy EUCLIDEAN_DISTANCE
            = (source, dest) -> Math.sqrt(
                    Math.pow(dest.getLatitude() - source.getLatitude(), 2)
                    + Math.pow(dest.getLongitude() - source.getLongitude(), 2)
            );

    public static final EdgeWeightStrategy UNIT_WEIGHT
            = (source, dest) -> 1.0;

    private WeightStrategies() {
        throw new AssertionError("No instances");
    }
}
