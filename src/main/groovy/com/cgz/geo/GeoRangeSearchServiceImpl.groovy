package com.cgz.geo

import com.cgz.geomath.Point
import com.cgz.geomath.PointPair
import com.cgz.routing.RouteRefinementService
import com.cgz.routing.TravelMode

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.stream.Collectors

class GeoRangeSearchServiceImpl implements GeoRangeSearchService {

    private RouteRefinementService routingService

    private ExecutorService executor = Executors.newFixedThreadPool(10)

    private InitialPointsService initialPointsDistributionService

    GeoRangeSearchServiceImpl(RouteRefinementService routingService,
                              InitialPointsService initialPointsDistributionService) {
        this.routingService = routingService
        this.initialPointsDistributionService = initialPointsDistributionService
    }

    @Override
    List<Point> search(Point start, long timeInMinutes, TravelMode travelMode) {


        List<PointPair> initialPoints = initialPointsDistributionService.getInitialPoints(start, timeInMinutes, travelMode)

        List<Callable<Point>> routingServiceTasks = generateRoutingServiceTasks(initialPoints, timeInMinutes, travelMode)

        executor.invokeAll(routingServiceTasks).collect { it.get() }
    }


    private List<Callable<Point>> generateRoutingServiceTasks(List<PointPair> initialPoints, long timeInMinutes, TravelMode travelMode) {
        initialPoints.stream().map({ pointPair ->
            new Callable<Point>() {
                @Override
                Point call() throws Exception {
                    return routingService.refinePoints(pointPair, timeInMinutes, travelMode)
                }
            }
        }).collect(Collectors.toList())
    }

}


