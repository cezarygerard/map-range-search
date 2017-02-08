package com.cgz.geo

import com.cgz.geomath.GeoMath
import com.cgz.geomath.Point
import com.cgz.geomath.PointPair
import com.cgz.routing.RouteRefinementService
import spock.lang.Specification

import static com.cgz.Location.PARIS
import static com.cgz.Location.WARSAW_POINT
import static com.cgz.geo.GeoServiceImpl.NUMBER_OF_POINTS_FOR_HOUR_SEARCH
import static org.assertj.core.api.Assertions.assertThat

/**
 * Created by czarek on 04.02.17.
 */
class GeoServiceImplTest extends Specification {

    RouteRefinementService routingService = Mock(RouteRefinementService)

    GeoMath geoMath = Mock(GeoMath)

    GeoServiceImpl geoService = new GeoServiceImpl(routingService, geoMath)

    PointPair somePointPair = new PointPair(PARIS.asPoint(), PARIS.asPoint(), 0, 0)

    final long ZERO_MINUTES = 0

    final long HOUR = 60

    def "Returns initial points"() {
        when:
        def points = geoService.getInitialPoints(PARIS.asPoint(), HOUR)
        then:
        assertThat(points).isNotEmpty()
    }

    def "For time limit of 0 returns single element list of initial points"() {
        when:
        List<PointPair> points = geoService.getInitialPoints(PARIS.asPoint(), ZERO_MINUTES)
        then:
        1 * geoMath.getNewPointPair(PARIS.asPoint(), _, _) >> somePointPair
        assertThat(points).hasSize(1)
        assertThat(points.first()).isEqualTo(somePointPair)

    }

    def "For each of initial pints geoService executes refinement"() {
        when:
        List<Point> result = geoService.search(PARIS.asPoint(), HOUR)
        then:
        NUMBER_OF_POINTS_FOR_HOUR_SEARCH * routingService.refinePoints({ it.origin == PARIS.asPoint() }, {
            it == HOUR
        }) >> WARSAW_POINT
        NUMBER_OF_POINTS_FOR_HOUR_SEARCH * geoMath.getNewPointPair(PARIS.asPoint(), _, _) >> somePointPair
        result.size() == NUMBER_OF_POINTS_FOR_HOUR_SEARCH
        assertThat(result).containsOnly(WARSAW_POINT)
    }


}