package com.coffeecode.model.weight;

import com.coffeecode.model.LocationNode;

@FunctionalInterface
public interface EdgeWeightStrategy {

    double calculateWeight(LocationNode source, LocationNode destination);
}
