package com.cgz.routing

import com.jayway.jsonpath.JsonPath
import spock.lang.Shared
import spock.lang.Specification

import static com.cgz.Location.PARIS_POINT
import static com.cgz.Location.WARSAW_POINT

/**
 * Created by czarek on 05.02.17.
 */
class GoogleMapsApiTest extends Specification {

    @Shared
            apiKey = 'someKey'
    @Shared
            apiHost = 'http://host'
    def tripTimeInMinutes = 860

    String jsonString = """{"routes":[{"legs":[{"duration":{"text":"14 hours 20 mins","value":${
        tripTimeInMinutes * 60
    }}}]}]}""".toString()

    def json = JsonPath.parse(jsonString)

    GoogleMapsApi googleMapsApi = Spy(GoogleMapsApi, constructorArgs: [apiKey, apiHost]) {
        httpGetForJson(_) >> json
    }

    def "test spy works"() {
        when:
        def jsonFromSpy = googleMapsApi.httpGetForJson(new URL("http://some.url"))

        then:
        json == jsonFromSpy
    }

    def "url is constructed"() {
        when:
        googleMapsApi.travelTimeInMinutes(WARSAW_POINT, PARIS_POINT)

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
        when:
        def travelTimeInMinutes = googleMapsApi.travelTimeInMinutes(WARSAW_POINT, PARIS_POINT)

        then:
        travelTimeInMinutes == tripTimeInMinutes
    }


}
