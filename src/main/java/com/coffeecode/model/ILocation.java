package com.coffeecode.model;

public interface ILocation {

    Long getId();

    String getName();

    double getLongitude();

    double getLatitude();

    @Override
    java.lang.String toString();

    ILocation setName(String name);

    double distanceTo(Location other);

    String getDisplayName();

    boolean isSameLocation(Location other);

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

}
