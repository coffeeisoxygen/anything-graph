package com.coffeecode.model;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@ToString
@Builder
public class Location {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    private final Long id;

    @NotBlank(message = "Location name cannot be blank")
    @Size(min = 2, max = 50)
    private final String name;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private final double longitude;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private final double latitude;

    @Builder
    public Location(String name, double longitude, double latitude) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static void resetIdGenerator() {
        ID_GENERATOR.set(1);
    }

    public double distanceTo(Location other) {
        final double R = 6371; // Radius of Earth in km
        double lat1 = Math.toRadians(this.latitude);
        double lon1 = Math.toRadians(this.longitude);
        double lat2 = Math.toRadians(other.latitude);
        double lon2 = Math.toRadians(other.longitude);

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Result in kilometers
    }

    public String getDisplayName() {
        return String.format("%s (%.2f, %.2f)", name, latitude, longitude);
    }

    public boolean isSameLocation(Location other) {
        if (other == null) {
            return false;
        }
        final double EPSILON = 0.0001; // Approximately 11 meters at equator
        return Math.abs(this.latitude - other.latitude) < EPSILON
                && Math.abs(this.longitude - other.longitude) < EPSILON;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Location)) {
            return false;
        }
        Location location = (Location) o;
        return id != null && id.equals(location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
