package com.cgz.routing

import com.cgz.geomath.Point
import com.cgz.geomath.PointPair

/**
 * Created by czarek on 04.02.17.
 */
interface RouteRefinementService {

    Point refinePoints(PointPair originAndDestination, double routTimeLimitInMinutes, TravelMode travelMode)

}