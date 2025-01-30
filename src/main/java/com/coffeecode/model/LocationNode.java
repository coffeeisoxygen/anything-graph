package com.coffeecode.model;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode(of = "id")
public class LocationNode {

    @NonNull
    @NotBlank
    private final String id;

    private final double latitude;
    private final double longitude;

    public LocationNode(@NonNull String id) {
        this(id, 0.0, 0.0);
    }

    public LocationNode(@NonNull String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Node{id='" + id + "', latitude=" + latitude + ", longitude=" + longitude + '}';
    }
}
