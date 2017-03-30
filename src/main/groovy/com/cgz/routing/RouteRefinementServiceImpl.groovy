package com.cgz.routing

import com.cgz.geomath.GeoMath
import com.cgz.geomath.Point
import com.cgz.geomath.PointPair
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RouteRefinementServiceImpl implements RouteRefinementService {

    static final int MAX_ATTEMPTS = 5

    private static final double CLOSE_ENOUGH_RESULT_EPSILON = 0.05

    private RoutingService routingService

    private GeoMath geoMath

    private static final Logger logger = LoggerFactory.getLogger(RouteRefinementServiceImpl);

    @Autowired
    RouteRefinementServiceImpl(RoutingService routingService, GeoMath geoMath) {
        this.routingService = routingService
        this.geoMath = geoMath
    }

    @Override
    Point refinePoints(PointPair originAndDestination, double timeLimitInMinutes, TravelMode travelMode) {

        Optional<Double> travelTimeInMinutes = executeRoutingService(originAndDestination.origin, originAndDestination.destination, travelMode)

        int attempts = 1

        PointPair lastDestination = originAndDestination

        while (shouldRefine(timeLimitInMinutes, travelTimeInMinutes, attempts)) {
            PointPair newDestination = calculateNewDestination(lastDestination,
                    travelTimeInMinutes,
                    timeLimitInMinutes)

            travelTimeInMinutes = executeRoutingService(originAndDestination.origin, newDestination.destination, travelMode)

            lastDestination = newDestination

            attempts++
        }

        return lastDestination.destination
    }

    private PointPair calculateNewDestination(PointPair lastOriginAndDestination,
                                              Optional<Double> optionalTravelTimeInMinutes,
                                              double timeLimitInMinutes) {


        optionalTravelTimeInMinutes.map({ travelTimeInMinutes ->
            double distance = lastOriginAndDestination.distanceInMeters
            double velocity = distance / travelTimeInMinutes
            double newDistance = timeLimitInMinutes * velocity
            geoMath.getNewPointPair(lastOriginAndDestination.origin, newDistance, lastOriginAndDestination.azimuthInDegres)
        }).orElse(lastOriginAndDestination)
    }

    private Optional<Double> executeRoutingService(Point origin, Point newDestination, TravelMode travelMode) {
        try {
            return Optional.of(routingService.travelTimeInMinutes(origin, newDestination, travelMode))
        } catch (RoutingServiceException rse) {
            logger.warn("routingService travelTimeInMinutes failed, arguments: {} {} {}",
                    origin,
                    newDestination,
                    travelMode,
                    rse
            )

            return Optional.empty()
        }
    }

    private boolean shouldRefine(double desired, Optional<Double> optionalTime, int attempts) {
        double actual = optionalTime.orElse(Double.valueOf(0.0))
        boolean a = (1 - CLOSE_ENOUGH_RESULT_EPSILON) < (actual / desired)
        boolean b = (actual / desired) < 1 + CLOSE_ENOUGH_RESULT_EPSILON
        boolean withinEpsilon = a && b
        boolean attemptsNotExceeded = attempts < MAX_ATTEMPTS
        ((!withinEpsilon) && attemptsNotExceeded)
    }
}

