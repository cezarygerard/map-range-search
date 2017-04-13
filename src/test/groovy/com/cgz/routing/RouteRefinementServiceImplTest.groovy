package com.cgz.routing

import com.cgz.Location
import com.cgz.geomath.GeoMath
import com.cgz.geomath.PointPair
import spock.lang.Specification

class RouteRefinementServiceImplTest extends Specification {

    RoutingService routingService = Mock(RoutingService)

    GeoMath geoMath = Mock(GeoMath)

    RouteRefinementServiceImpl routeRefinementService = new RouteRefinementServiceImpl(routingService, geoMath)

    def AZIMUTH = 260

    def DISTANCE = 1_500_000

    def HOUR = 60

    PointPair someOriginAndDest = new PointPair(Location.WARSAW_POINT, Location.PARIS.asPoint(), DISTANCE, AZIMUTH)

    TravelMode anyTravelMode = TravelMode.BICYCLING

    def "Call routing service for travelTimes"() {
        when:
        routeRefinementService.refinePoints(someOriginAndDest, HOUR, anyTravelMode)

        then:
        1 * routingService.travelTimeInMinutes(someOriginAndDest.origin, someOriginAndDest.destination, anyTravelMode) >> HOUR
    }

    def "stops refining when result is good enough"() {
        when:
        routeRefinementService.refinePoints(someOriginAndDest, HOUR, anyTravelMode)

        then:
        3 * routingService.travelTimeInMinutes(someOriginAndDest.origin, someOriginAndDest.destination, anyTravelMode) >>> [2 * HOUR, 3 * HOUR, 1 * HOUR]
        2 * geoMath.getNewPointPair(_, _, AZIMUTH) >> someOriginAndDest
    }

    def "refines destination"() {
        given:
        routingService.travelTimeInMinutes(someOriginAndDest.origin, someOriginAndDest.destination, anyTravelMode) >>> [15 * HOUR, 10 * HOUR, 5 * HOUR, 1 * HOUR]

        when:
        routeRefinementService.refinePoints(someOriginAndDest, HOUR, anyTravelMode)

        then:
        3 * geoMath.getNewPointPair(_, _, AZIMUTH) >>> [
                new PointPair(someOriginAndDest.origin, someOriginAndDest.destination, DISTANCE, AZIMUTH),
                new PointPair(someOriginAndDest.origin, someOriginAndDest.destination, DISTANCE / 2, AZIMUTH),
                new PointPair(someOriginAndDest.origin, someOriginAndDest.destination, DISTANCE / 5, AZIMUTH)
        ]
    }

    def "limits searches"() {
        given:
        routingService.travelTimeInMinutes(someOriginAndDest.origin, someOriginAndDest.destination, anyTravelMode) >> 2 * HOUR

        when:
        routeRefinementService.refinePoints(someOriginAndDest, HOUR, anyTravelMode)

        then:
        (RouteRefinementServiceImpl.MAX_ATTEMPTS - 1) * geoMath.getNewPointPair(someOriginAndDest.origin, _, AZIMUTH) >> someOriginAndDest
        (RouteRefinementServiceImpl.MAX_ATTEMPTS) * routingService.travelTimeInMinutes(someOriginAndDest.origin, someOriginAndDest.destination, anyTravelMode)

    }

//    TODO TEST optionalTravelTimeInMinutes == 0
//    TODO TEST error haning
}


