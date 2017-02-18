package com.cgz.routing

import com.cgz.geomath.Point
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Created by czarek on 04.02.17.
 */
@Service
class GoogleMapsApi implements RoutingService {

    private final String urlTemplate

    private static final String DURATION_JSON_PATH = 'routes[0].legs[0].duration.value'

    private static volatile int counter = 0

    @Autowired
    GoogleMapsApi(@Value('${GoogleMapsApi.apiKey}') String apikey, @Value('${GoogleMapsApi.apiHost}') String apiHost) {
        urlTemplate = "${apiHost}/maps/api/directions/json?key=${apikey}&alternatives=false&traffic_model=optimistic&departure_time=now&"
    }

    @Override
    long travelTimeInMinutes(Point origin, Point destination, TravelMode travelMode) {

        try {
            URL url = constructUrl(origin, destination, travelMode)
            DocumentContext json = httpGetForJson(url)
            long timeInMinutes = readTimeInMinutes(json)
            return timeInMinutes
        } catch (Exception e) {
            e.printStackTrace()
            throw new RoutingServiceException("Could not read json", e)
        }
    }

    private long readTimeInMinutes(DocumentContext json) {
        try {
            long timeInSeconds = json.read(DURATION_JSON_PATH)
            return timeInSeconds / 60
        } catch (Exception e) {
            println json.jsonString()
            throw new RoutingServiceException("Could not read json ${json.jsonString()}", e)
        }
    }

    protected DocumentContext httpGetForJson(URL url) {
        counter++
        println("Executed GoogleMapsApi: " + counter)
        url.withInputStream { inputStream ->
            JsonPath.parse(inputStream)
        }
    }

    private URL constructUrl(Point origin, Point destination, TravelMode travelMode) {
        String urlString = urlTemplate + "&origin=${origin.lat},${origin.lng}" +
                "&destination=${destination.lat},${destination.lng}" +
                "&mode=${travelMode.value}".toString()

        new URL(urlString)
    }
}
