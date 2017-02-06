package com.cgz.geomath

/**
 * Created by czarek on 05.02.17.
 */
interface GeoMath {

    PointPair getNewPointPair(Point origin, double distanceInMeters, double azimuth)

}