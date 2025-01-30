package com.coffeecode.model;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@ToString
@Builder
public class Location implements ILocation {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    private final Long id;

    @Setter // Only name can be modified
    @NotBlank(message = "Location name cannot be blank")
    @Size(min = 2, max = 50)
    private String name;

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

    @Override
    public double distanceTo(Location other) {
        double dx = this.longitude - other.longitude;
        double dy = this.latitude - other.latitude;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String getDisplayName() {
        return String.format("%s (%.2f, %.2f)", name, latitude, longitude);
    }

    @Override
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
