package com.cgz.routing.googlemaps

import com.cgz.geomath.Point
import com.cgz.routing.RoutingService
import com.cgz.routing.RoutingServiceException
import com.cgz.routing.TravelMode
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Created by czarek on 04.02.17.
 */
@Service
class GoogleMapsRoutingService implements RoutingService {

    private final String urlTemplate

    protected static final JsonPath DURATION_JSON_PATH = JsonPath.compile('routes[0].legs[0].duration.value')

    private static volatile int counter = 0

    protected Configuration jsonPrserConfiguration

    @Autowired
    GoogleMapsRoutingService(
            @Value('${GoogleMapsApi.apiKey}') String apikey,
            @Value('${GoogleMapsApi.apiHost}') String apiHost) {

        urlTemplate = "${apiHost}/maps/api/directions/json?key=${apikey}&alternatives=false&traffic_model=optimistic&departure_time=now&"

        this.jsonPrserConfiguration = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
    }

    @Override
    long travelTimeInMinutes(Point origin, Point destination, TravelMode travelMode) {

        invokeService(origin, destination, travelMode)
    }

    private long invokeService(Point origin, Point destination, TravelMode travelMode) {
        try {
            URL url = constructUrl(origin, destination, travelMode)
            DocumentContext json = httpGetForJson(url)
            long timeInMinutes = readTimeInMinutes(json)
            return timeInMinutes
        } catch (RoutingServiceException rse) {
            throw rse
        } catch (Exception e) {
            throw new RoutingServiceException("Could not read json", e)
        }
    }

    private long readTimeInMinutes(DocumentContext json) {
        try {
            //TODO https://github.com/json-path/JsonPath/issues/78
            //com.jayway.jsonpath.Configuration
            //TODO read no routes present
            Long timeInSeconds = json.read(DURATION_JSON_PATH)
            if (timeInSeconds == null) {
                return Long.MAX_VALUE
            }
            return timeInSeconds / 60
        } catch (Exception e) {
            throw new RoutingServiceException("Could not read json ${json.jsonString()}", e)
        }
    }

    protected DocumentContext httpGetForJson(URL url) {
        counter++
        println("Executed GoogleMapsApi: " + counter)
        url.withInputStream { inputStream ->
            JsonPath.parse(inputStream, jsonPrserConfiguration)
        }
    }

    private URL constructUrl(Point origin, Point destination, TravelMode travelMode) {
        String urlString = urlTemplate + "&origin=${origin.lat},${origin.lng}" +
                "&destination=${destination.lat},${destination.lng}" +
                "&mode=${travelMode.name}".toString()

        new URL(urlString)
    }
}
