package com.cgz.routing

import com.cgz.routing.googlemaps.GoogleMapsApi
import com.cgz.routing.googlemaps.RouteCache
import com.jayway.jsonpath.JsonPath
import spock.lang.Shared
import spock.lang.Specification

import static com.cgz.Location.PARIS_POINT
import static com.cgz.Location.WARSAW_POINT

class GoogleMapsApiTest extends Specification {

    @Shared
    String apiKey = 'someKey'

    @Shared
    String apiHost = 'http://host'

    RouteCache routeCache = Mock(RouteCache)

    def tripTimeInMinutes = 860

    TravelMode anyTravelMode = TravelMode.WALKING

    String jsonString = """{"routes":[{"legs":[{"duration":{"text":"14 hours 20 mins","value":${
        tripTimeInMinutes * 60
    }}}]}]}""".toString()

    def json = JsonPath.parse(jsonString)

    GoogleMapsApi googleMapsApi = Spy(GoogleMapsApi, constructorArgs: [apiKey, apiHost, routeCache]) {
        httpGetForJson(_) >> json
    }

    def "test spy works"() {
        when:
        def jsonFromSpy = googleMapsApi.httpGetForJson(new URL("http://some.url"))

        then:
        json == jsonFromSpy
    }

    def "url is constructed"() {
        given:
        routeCache.get(_, _) >> Optional.empty()

        when:
        googleMapsApi.travelTimeInMinutes(WARSAW_POINT, PARIS_POINT, anyTravelMode)

        then:
        googleMapsApi.httpGetForJson({ it.toString().contains(urlFragment) }) >> json

        where:
        urlFragment << ["https://${apiHost}/maps/api/directions/json?",
                        "key=${apiKey}",
                        "&alternatives=false&traffic_model=optimistic&departure_time=now",
                        "&origin=${WARSAW_POINT.lat},${WARSAW_POINT.lng}",
                        "&destination=${PARIS_POINT.lat},${PARIS_POINT.lng}"
        ]
    }

    def "travel Time is extracted"() {
        given:
        routeCache.get(_, _) >> Optional.empty()

        when:
        def travelTimeInMinutes = googleMapsApi.travelTimeInMinutes(WARSAW_POINT, PARIS_POINT, anyTravelMode)

        then:
        travelTimeInMinutes == tripTimeInMinutes
    }

    def "travel time estimation tries to get from cache"() {
        when:
        def travelTimeInMinutes = googleMapsApi.travelTimeInMinutes(WARSAW_POINT, PARIS_POINT, anyTravelMode)

        then:
        1 * routeCache.get(_, _) >> Optional.empty()
    }

    def "successful time estimation goes to cache"() {
        when:
        def travelTimeInMinutes = googleMapsApi.travelTimeInMinutes(WARSAW_POINT, PARIS_POINT, anyTravelMode)

        then:
        1 * routeCache.get(_, _) >> Optional.empty()
        1 * routeCache.put(WARSAW_POINT, PARIS_POINT, tripTimeInMinutes)
    }


}
