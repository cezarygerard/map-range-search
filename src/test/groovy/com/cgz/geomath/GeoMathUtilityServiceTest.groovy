package com.cgz.geomath

import org.assertj.core.data.Percentage
import spock.lang.Specification

import static com.cgz.Location.WARSAW
import static org.assertj.core.api.Assertions.assertThat

/**
 * Created by czarek on 04.02.17.
 */
class GeoMathUtilityServiceTest extends Specification {

    private GeoMathUtilityService geoMath = new GeoMathUtilityService()

    def "Returns same point for distance of zero kilometers"() {
        when:
        def pointPair = geoMath.getNewPointPair(WARSAW.asPoint(), distance, angle)
        then:
        pointPair.destination.lat == newLat
        pointPair.destination.lng == newlng
        where:
        distance | angle   || newLat     | newlng
        0        | 0       || WARSAW.lat | WARSAW.lng
        0        | Math.PI || WARSAW.lat | WARSAW.lng
    }

    def "Return close longitude for 0 and 180 degrees"() {
        when:
        def pointPair = geoMath.getNewPointPair(WARSAW.asPoint(), distance, angle)
        then:
        assertThat(pointPair.destination.lng).isCloseTo(WARSAW.lng, Percentage.withPercentage(0.001))
        where:
        distance | angle || newlng
        1500     | 0     || WARSAW.lng
        1500     | 180   || WARSAW.lng
    }

    def "Return close latitude for PI/2 and PI *1.5  radian angles"() {
        when:
        def pointPair = geoMath.getNewPointPair(WARSAW.asPoint(), distance, angle)
        then:
        assertThat(pointPair.destination.lat).isCloseTo(WARSAW.lat, Percentage.withPercentage(0.001))
        where:
        distance | angle || newlat
        1500     | 90    || WARSAW.lat
        1500     | 270   || WARSAW.lat
    }
}

