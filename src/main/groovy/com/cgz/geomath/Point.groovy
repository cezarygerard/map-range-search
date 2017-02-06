package com.cgz.geomath

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString


/**
 * Created by czarek on 03.02.17.
 */
@EqualsAndHashCode
@ToString
final class Point {
    final double lat
    final double lng

    Point(double lat, double lng) {
        this.lat = lat
        this.lng = lng
    }


}

