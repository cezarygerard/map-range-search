package com.cgz.geo

import com.cgz.geomath.Point
import com.cgz.routing.TravelMode

/**
 * Created by czarek on 04.02.17.
 */
interface GeoRangeSearchService {
    List<Point> search(Point start, long timeInMinutes, TravelMode travelMode)

}