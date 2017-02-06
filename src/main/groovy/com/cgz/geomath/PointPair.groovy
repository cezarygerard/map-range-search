package com.cgz.geomath

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Created by czarek on 04.02.17.
 */
@EqualsAndHashCode
@ToString
final class PointPair {

    final Point origin
    final Point destination
    final double distanceInMeters
    final double azimuthInDegres

    PointPair(Point origin, Point destination, double distanceInMeters, double azimuthInDegres) {
        this.origin = origin
        this.destination = destination
        this.distanceInMeters = distanceInMeters
        this.azimuthInDegres = azimuthInDegres
    }
}
