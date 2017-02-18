package com.cgz.routing

import com.cgz.geomath.Point

/**
 * Created by czarek on 04.02.17.
 */
interface RoutingService {

    long travelTimeInMinutes(Point origin, Point destination, TravelMode travelMode)

}