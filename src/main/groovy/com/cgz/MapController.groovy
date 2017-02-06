package com.cgz

import com.cgz.geo.GeoServiceImpl
import com.cgz.geomath.Point
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by czarek on 03.02.17.
 */
@RestController
class MapController {

    private GeoServiceImpl geoService

    @Autowired
    MapController(GeoServiceImpl geoService) {
        this.geoService = geoService
    }

    @RequestMapping(path = "/timedRange", method = RequestMethod.GET)
    List<Point> getPoints(
            @RequestParam double latitude, @RequestParam double longitude, @RequestParam long timeInMinutes) {
        def millis = System.currentTimeMillis()
        def result = geoService.search(new Point(latitude, longitude), timeInMinutes)
        println(System.currentTimeMillis() - millis)
        return result
    }
}
