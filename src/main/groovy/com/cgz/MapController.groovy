package com.cgz

import com.cgz.geo.GeoRangeSearchServiceImpl
import com.cgz.geomath.Point
import com.cgz.routing.TravelMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MapController {

    static private final Logger logger = LoggerFactory.getLogger(MapController)

    private GeoRangeSearchServiceImpl geoService

    MapController(GeoRangeSearchServiceImpl geoService) {
        this.geoService = geoService
    }

    List<Point> getPoints(double latitude,
                          double longitude,
                          long timeInMinutes,
                          String travelMode
    ) {

        def millis = System.currentTimeMillis()

        Optional<TravelMode> mode = TravelMode.fromString(travelMode)
        Point from = new Point(latitude, longitude)

        def result = mode.map({ tm -> geoService.search(from, timeInMinutes, tm) })
                .orElseGet({
            throw new RuntimeException()
        })

        logger.info("${System.currentTimeMillis() - millis}")
        return result
    }

    List<Point> getPoints(Map<String, String> queryParams) {
        def latitude = Double.parseDouble(queryParams.get("latitude"))
        def longitude = Double.parseDouble(queryParams.get("longitude"))
        def timeInMinutes = Long.parseLong(queryParams.get("timeInMinutes"))
        def travelMode = queryParams.get("travelMode")

        return getPoints(latitude, longitude, timeInMinutes, travelMode)
    }
}
