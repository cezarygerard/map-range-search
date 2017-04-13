package com.cgz.geo

import com.cgz.geomath.GeoMath
import com.cgz.geomath.PointPair
import com.cgz.routing.TravelMode
import spock.lang.Shared
import spock.lang.Specification

import static com.cgz.Location.PARIS
import static com.cgz.geo.InitialPointsServiceImpl.getNUMBER_OF_POINTS_FOR_HOUR_SEARCH
import static org.assertj.core.api.Assertions.assertThat

class InitialPointsServiceImplTest extends Specification {

    long ZERO_MINUTES = 0

    @Shared
    long HOUR = 60

    TravelMode anyTravelMode = TravelMode.DRIVING

    GeoMath geoMath = Mock(GeoMath)

    InitialPointsServiceImpl service = new InitialPointsServiceImpl(geoMath)

    PointPair somePointPair = new PointPair(PARIS.asPoint(), PARIS.asPoint(), 0, 0)

    def "Returns initial points"() {
        when:
        def points = service.getInitialPoints(PARIS.asPoint(), HOUR, anyTravelMode)

        then:
        assertThat(points).isNotEmpty()
    }

    def "For time limit of 0 returns single element list of initial points"() {
        when:
        List<PointPair> points = service.getInitialPoints(PARIS.asPoint(), ZERO_MINUTES, anyTravelMode)

        then:
        1 * geoMath.getNewPointPair(PARIS.asPoint(), _, _) >> somePointPair
        assertThat(points).hasSize(1)
        assertThat(points.first()).isEqualTo(somePointPair)
    }

    def "return correct number of initial points"() {
        when:
        List<PointPair> points = service.getInitialPoints(PARIS.asPoint(), time, anyTravelMode)

        then:
        assertThat(points).hasSize((int) numberOfPoints)

        where:
        time     || numberOfPoints
        HOUR     || NUMBER_OF_POINTS_FOR_HOUR_SEARCH
        2 * HOUR || 2 * NUMBER_OF_POINTS_FOR_HOUR_SEARCH
    }

    def "calculates initial distance based on time and travel mode"() {
        when:
        service.getInitialPoints(PARIS.asPoint(), time, travelMode)

        then:
        _ * geoMath.getNewPointPair(PARIS.asPoint(), initialDistance, _)

        where:
        time     | travelMode           || initialDistance
        HOUR     | TravelMode.DRIVING   || TravelMode.DRIVING.defaultSpeedPerMinute
        2 * HOUR | TravelMode.WALKING   || 2 * TravelMode.WALKING.defaultSpeedPerMinute
        3 * HOUR | TravelMode.BICYCLING || 3 * TravelMode.BICYCLING.defaultSpeedPerMinute

    }
}
