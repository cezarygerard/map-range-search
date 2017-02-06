package com.cgz.routing

import com.cgz.geomath.GeoMath
import com.cgz.geomath.Point
import com.cgz.geomath.PointPair
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by czarek on 04.02.17.
 */
@Service
class RouteRefinementServiceImpl implements RouteRefinementService {

    private static final int MAX_ATTEMPTS = 5
    private static final double CLOSE_ENOUGH_RESULT_EPSILON = 0.1

    private RoutingService routingService

    private GeoMath geoMath


    @Autowired
    RouteRefinementServiceImpl(RoutingService routingService, GeoMath geoMath) {
        this.routingService = routingService
        this.geoMath = geoMath
    }

    @Override
    Point refinePoints(PointPair originAndDestination, double timeLimitInMinutes) {

        double travelTimeInMinutes = executeRoutingService(originAndDestination.origin, originAndDestination.destination)

        int attempts = 1

        PointPair lastDestination = originAndDestination

        while (shouldRefine(timeLimitInMinutes, travelTimeInMinutes, attempts)) {
            PointPair newDestination = calculateNewDestination(lastDestination,
                    travelTimeInMinutes,
                    timeLimitInMinutes)

            travelTimeInMinutes = executeRoutingService(originAndDestination.origin, newDestination.destination)

            lastDestination = newDestination

            attempts++
        }

        return lastDestination.destination
    }

    private PointPair calculateNewDestination(PointPair lastOriginAndDestination,
                                              double travelTimeInMinutes,
                                              double timeLimitInMinutes) {

        double distance = lastOriginAndDestination.distanceInMeters
        double velocity = distance / travelTimeInMinutes
        double newDistance = timeLimitInMinutes * velocity
        geoMath.getNewPointPair(lastOriginAndDestination.origin, newDistance, lastOriginAndDestination.azimuthInDegres)
    }

    private double executeRoutingService(Point origin, Point newDestination) {
        return routingService.travelTimeInMinutes(origin, newDestination)
    }

    private boolean shouldRefine(double desired, double actual, int attempts) {
        boolean a = (1 - CLOSE_ENOUGH_RESULT_EPSILON) < (actual / desired)
        boolean b = (actual / desired) < 1 + CLOSE_ENOUGH_RESULT_EPSILON
        boolean withinEpsilon = a && b
        boolean attemptsNotExeeded = attempts < MAX_ATTEMPTS
        (!withinEpsilon) && attemptsNotExeeded
    }
}

