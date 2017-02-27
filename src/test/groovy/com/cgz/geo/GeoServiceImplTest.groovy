package com.cgz.geo

import com.cgz.geomath.Point
import com.cgz.geomath.PointPair
import com.cgz.routing.RouteRefinementService
import com.cgz.routing.TravelMode
import com.google.common.collect.Lists
import spock.lang.Specification

import static com.cgz.Location.PARIS
import static com.cgz.Location.WARSAW_POINT
import static org.assertj.core.api.Assertions.assertThat

class GeoServiceImplTest extends Specification {

    RouteRefinementService routingService = Mock(RouteRefinementService)

    InitialPointsService initialPointsService = Mock(InitialPointsService)

    GeoServiceImpl geoService = new GeoServiceImpl(routingService, initialPointsService)

    PointPair somePointPair = new PointPair(PARIS.asPoint(), PARIS.asPoint(), 0, 0)

    TravelMode anyTravelMode = TravelMode.DRIVING

    final long HOUR = 60

    def "For each of initial pints geoService executes refinement"() {
        given:
        def initialPoints = Lists.newArrayList(somePointPair, somePointPair, somePointPair, somePointPair)
        initialPointsService.getInitialPoints(PARIS.asPoint(), HOUR, anyTravelMode) >> initialPoints

        when:
        List<Point> result = geoService.search(PARIS.asPoint(), HOUR, anyTravelMode)

        then:
        initialPoints.size() * routingService.refinePoints({ it.origin == PARIS.asPoint() },
                { it == HOUR }, { it == anyTravelMode }) >> WARSAW_POINT
        assertThat(result).containsOnly(WARSAW_POINT)
    }


}