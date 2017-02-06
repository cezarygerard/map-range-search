package com.cgz.routing

import com.cgz.Location
import com.cgz.geomath.GeoMath
import com.cgz.geomath.PointPair
import spock.lang.Specification

/**
 * Created by czarek on 05.02.17.
 */
class RouteRefinementServiceImplTest extends Specification {

    RoutingService routingService = Mock(RoutingService)

    GeoMath geoMath = Mock(GeoMath)

    RouteRefinementServiceImpl routeRefinementService = new RouteRefinementServiceImpl(routingService, geoMath)

    def AZIMUTH = 260

    def DISTANCE = 1_500_000

    def HOUR = 60

    PointPair someOriginAndDest = new PointPair(Location.WARSAW_POINT, Location.PARIS.asPoint(), DISTANCE, AZIMUTH)

    def "Call routing service for travelTimes"() {
        when:
        routeRefinementService.refinePoints(someOriginAndDest, HOUR)

        then:
        1 * routingService.travelTimeInMinutes(someOriginAndDest.origin, someOriginAndDest.destination) >> HOUR
    }

    def "stops refining when result is good enough"() {
        when:
        routeRefinementService.refinePoints(someOriginAndDest, HOUR)

        then:
        4 * routingService.travelTimeInMinutes(someOriginAndDest.origin, _) >>> [3 * HOUR, 2 * HOUR, 0.5 * HOUR, 1 * HOUR]
        3 * geoMath.getNewPointPair(_, _, AZIMUTH) >> someOriginAndDest
    }

    def "refines destination"() {
        given:
        routingService.travelTimeInMinutes(someOriginAndDest.origin, _) >>> [15 * HOUR, 10 * HOUR, 5 * HOUR, 1 * HOUR]

        when:
        routeRefinementService.refinePoints(someOriginAndDest, HOUR)

        then:
        3 * geoMath.getNewPointPair(_, _, AZIMUTH) >>> [
                new PointPair(someOriginAndDest.origin, someOriginAndDest.destination, DISTANCE, AZIMUTH),
                new PointPair(someOriginAndDest.origin, someOriginAndDest.destination, DISTANCE / 2, AZIMUTH),
                new PointPair(someOriginAndDest.origin, someOriginAndDest.destination, DISTANCE / 5, AZIMUTH)
        ]
    }

    def "limits searches"() {
        given:
        routingService.travelTimeInMinutes(someOriginAndDest.origin, _) >> 2 * HOUR

        when:
        routeRefinementService.refinePoints(someOriginAndDest, HOUR)

        then:
        (RouteRefinementServiceImpl.MAX_ATTEMPTS - 1) * geoMath.getNewPointPair(_, _, AZIMUTH) >> someOriginAndDest
        (RouteRefinementServiceImpl.MAX_ATTEMPTS) * routingService.travelTimeInMinutes(someOriginAndDest.origin, _)

    }


}

