package com.cgz

import com.cgz.geo.GeoRangeSearchServiceImpl
import com.cgz.geomath.Point
import com.cgz.routing.TravelMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse

@RestController
class MapController {

    static private final Logger logger = LoggerFactory.getLogger(MapController)

    private GeoRangeSearchServiceImpl geoService

    @Autowired
    MapController(GeoRangeSearchServiceImpl geoService) {
        this.geoService = geoService
    }

    @RequestMapping(path = "/timedRange", method = RequestMethod.GET)
    List<Point> getPoints(@RequestParam double latitude,
                          @RequestParam double longitude,
                          @RequestParam long timeInMinutes,
                          @RequestParam String travelMode
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

    private List<Point> handleInvalidRequest(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST)
        Collections.<Point> emptyList()
    }

    List<Point> getPoints(Map<String, String> queryParams) {
        def latitude = Double.parseDouble(queryParams.get("latitude"))
        def longitude = Double.parseDouble(queryParams.get("longitude"))
        def timeInMinutes = Long.parseLong(queryParams.get("timeInMinutes"))
        def travelMode = queryParams.get("travelMode")

        return getPoints(latitude, longitude, timeInMinutes, travelMode)
    }
}
