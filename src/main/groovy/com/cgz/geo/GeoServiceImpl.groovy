package com.cgz.geo

import com.cgz.geomath.GeoMath
import com.cgz.geomath.Point
import com.cgz.geomath.PointPair
import com.cgz.routing.RouteRefinementService
import groovy.transform.PackageScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.concurrent.ForkJoinPool
import java.util.stream.Collectors
import java.util.stream.DoubleStream

/**
 * Created by czarek on 04.02.17.
 */
@Service
class GeoServiceImpl implements GeoService {

    static double STARTING_DISTANCE_IN_METERS_FOR_ONE_HOUR_SEARCH = 50000

    static int NUMBER_OF_POINTS_FOR_HOUR_SEARCH = 36

    private RouteRefinementService routingService

    private GeoMath geoMath

    @Autowired
    GeoServiceImpl(RouteRefinementService routingService, GeoMath geoMath) {

        this.routingService = routingService
        this.geoMath = geoMath
    }

    @Override
    List<Point> search(Point start, long timeInMinutes) {
        List<PointPair> initialPoints = getInitialPoints(start, timeInMinutes)

        ForkJoinPool forkJoinPool = new ForkJoinPool(20)

        List<Point> points = forkJoinPool.submit({
            initialPoints.stream().parallel()
                    .map({ pointPair -> new Tuple(pointPair.azimuthInDegres, routingService.refinePoints(pointPair, timeInMinutes)) })
                    .sorted({ Tuple p1, Tuple p2 -> Double.compare(p1[0], p2[0]) })
                    .map({ Tuple t -> t[1] })
                    .collect(Collectors.toList())
        }).get()

        return points
    }

    @PackageScope
    List<PointPair> getInitialPoints(Point startingPoint, long timeInMinutes) {
        int numberOfPoints = getNumberOfInitialsPoints(timeInMinutes)
        double degreesPerPoint = 360.0 / numberOfPoints
        double initialDistance = (STARTING_DISTANCE_IN_METERS_FOR_ONE_HOUR_SEARCH * timeInMinutes) / 60.0

        return calculateInitialPointsAroundStartingPoint(startingPoint, numberOfPoints, degreesPerPoint, initialDistance)
    }

    private int getNumberOfInitialsPoints(long timeInMinutes) {
        if (timeInMinutes <= 0) {
            return 1
        }

        NUMBER_OF_POINTS_FOR_HOUR_SEARCH * timeInMinutes / 60
    }

    private List<PointPair> calculateInitialPointsAroundStartingPoint(Point startingPoint, int numberOfPoints, double degreesPerPoint, double initialDistance) {
        return DoubleStream.iterate(0.0, { it -> it + degreesPerPoint })
                .limit(numberOfPoints)
                .mapToObj({ angle -> geoMath.getNewPointPair(startingPoint, initialDistance, angle) })
                .collect(Collectors.toList())
    }
}
