package com.cgz

import com.cgz.geo.GeoRangeSearchService
import com.cgz.geo.GeoRangeSearchServiceImpl
import com.cgz.geo.InitialPointsService
import com.cgz.geo.InitialPointsServiceImpl
import com.cgz.geomath.GeoMath
import com.cgz.geomath.GeoMathUtilityService
import com.cgz.routing.RouteRefinementService
import com.cgz.routing.RouteRefinementServiceImpl
import com.cgz.routing.RoutingService
import com.cgz.routing.googlemaps.GoogleMapsRoutingService

class UglyApplicationContext {

    private MapController mapController

    private static UglyApplicationContext instance

    private GeoMathUtilityService geoMath

    private UglyApplicationContext() {
        init()
    }

    static UglyApplicationContext getInstance() {
        if (instance == null) {
            synchronized (UglyApplicationContext.class) {
                if (instance == null) {
                    instance = new UglyApplicationContext()
                }
            }
        }
        return instance
    }

    private init() {
        this.mapController = new MapController(getGeoRangeSearchService())
    }

    MapController getMapController() {
        mapController
    }

    private GeoRangeSearchService getGeoRangeSearchService() {
        return new GeoRangeSearchServiceImpl(getRouteRefinementService(), getInitialPointsService())
    }

    private InitialPointsService getInitialPointsService() {
        new InitialPointsServiceImpl(getGeoMath())
    }

    private GeoMath getGeoMath() {
        if (geoMath == null) {
            this.geoMath = new GeoMathUtilityService()
        }
        geoMath
    }

    private RouteRefinementService getRouteRefinementService() {
        new RouteRefinementServiceImpl(getRoutingService(), getGeoMath())
    }

    private RoutingService getRoutingService() {
        Properties prop = new Properties()
        prop.load(this.class.getClassLoader().getResourceAsStream('application.properties'))
        new GoogleMapsRoutingService(prop.getProperty('GoogleMapsApi.apiKey'), prop.getProperty('GoogleMapsApi.apiHost'))
    }
}
