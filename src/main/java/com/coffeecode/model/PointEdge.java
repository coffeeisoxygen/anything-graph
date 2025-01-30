package com.coffeecode.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointEdge {

    @NonNull
    private PointNode source;

    @NonNull
    private PointNode target;

    @Builder.Default
    private double weight = 0.0;

    @Builder.Default
    private boolean visited = false;

    public PointEdge(@NonNull PointNode source, @NonNull PointNode target) {
        this.source = source;
        this.target = target;
        this.weight = calculateWeight();
    }

    private double calculateWeight() {
        double dx = target.getLongitude() - source.getLongitude();
        double dy = target.getLatitude() - source.getLatitude();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
