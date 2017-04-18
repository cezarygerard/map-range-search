package com.cgz.geo

import com.cgz.geomath.GeoMath
import com.cgz.geomath.Point
import com.cgz.geomath.PointPair
import com.cgz.routing.TravelMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.stream.Collectors
import java.util.stream.DoubleStream

@Service
class InitialPointsServiceImpl implements InitialPointsService {

    //TODO make it injectable
    static long NUMBER_OF_POINTS_FOR_HOUR_SEARCH = 54L

    //TODO make it injectable
    static long MIN_NUMBER_OF_POINTS = 36L

    private GeoMath geoMath

    @Autowired
    InitialPointsServiceImpl(GeoMath geoMath) {
        this.geoMath = geoMath
    }

    @Override
    List<PointPair> getInitialPoints(Point startingPoint, long timeInMinutes, TravelMode travelMode) {
        int numberOfPoints = getNumberOfInitialsPoints(timeInMinutes)
        double degreesPerPoint = 360.0 / numberOfPoints

        double initialDistance = timeInMinutes * travelMode.defaultSpeedPerMinute

        def var = calculateInitialPointsAroundStartingPoint(startingPoint, numberOfPoints, degreesPerPoint, initialDistance)
        return var;
    }


    private int getNumberOfInitialsPoints(long timeInMinutes) {
        if (timeInMinutes <= 0) {
            return 1
        }

        long numberOfPoints = NUMBER_OF_POINTS_FOR_HOUR_SEARCH * timeInMinutes / 60
        Math.max(MIN_NUMBER_OF_POINTS, numberOfPoints)
    }


    private List<PointPair> calculateInitialPointsAroundStartingPoint(Point startingPoint, int numberOfPoints, double degreesPerPoint, double initialDistance) {
        return DoubleStream.iterate(0.0, { it -> it + degreesPerPoint })
                .limit(numberOfPoints)
                .mapToObj({ angle -> geoMath.getNewPointPair(startingPoint, initialDistance, angle) })
                .collect(Collectors.toList())
    }


}
