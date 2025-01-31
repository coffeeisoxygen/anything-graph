package com.coffeecode.util;

public interface MapSearch {

    // Find the name of a location given its longitude and latitude
    String findName(double longitude, double latitude);

    // Find the longitude and latitude of a location given its name
    double[] findLongLat(String name);

}
