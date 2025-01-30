package com.coffeecode.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointNode {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotBlank(message = "Location name cannot be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotNull(message = "Longitude cannot be null")
    @DecimalMin(value = "-180.0") 
    @DecimalMax(value = "180.0")
    private double longitude;

    @NotNull(message = "Latitude cannot be null")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private double latitude;
    
    // For algorithm visualization
    @Builder.Default
    private boolean visited = false;
    
    @Builder.Default
    private PointNode parent = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointNode)) return false;
        PointNode that = (PointNode) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getDisplayName() {
        return String.format("%s (%.2f, %.2f)", name, latitude, longitude);
    }
}