package com.coffeecode.model.weight;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EdgeType {
    HAVERSINE("Distance Based"),
    EUCLIDEAN("Direct Line"),
    UNIT("Unit Weight");

    private final String displayName;
}
