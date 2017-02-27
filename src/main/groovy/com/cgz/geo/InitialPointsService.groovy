package com.cgz.geo

import com.cgz.geomath.Point
import com.cgz.geomath.PointPair
import com.cgz.routing.TravelMode


interface InitialPointsService {

    List<PointPair> getInitialPoints(Point startingPoint, long timeInMinutes, TravelMode travelMode)
}