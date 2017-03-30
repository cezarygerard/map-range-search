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

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Point point = (Point) o

        if (Double.compare(point.lat, lat) != 0) return false
        if (Double.compare(point.lng, lng) != 0) return false

        return true
    }

    int hashCode() {
        int result
        long temp
        temp = lat != +0.0d ? Double.doubleToLongBits(lat) : 0L
        result = (int) (temp ^ (temp >>> 32))
        temp = lng != +0.0d ? Double.doubleToLongBits(lng) : 0L
        result = 31 * result + (int) (temp ^ (temp >>> 32))
        return result
    }
}

