package com.cgz.geo

import com.cgz.geomath.Point

/**
 * Created by czarek on 04.02.17.
 */
interface GeoService {
    List<Point> search(Point start, long timeInMinutes)

}