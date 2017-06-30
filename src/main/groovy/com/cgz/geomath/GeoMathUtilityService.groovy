package com.cgz.geomath

/**
 * Created by czarek on 04.02.17.
 */
class GeoMathUtilityService implements GeoMath {

    private static final double EARTH_RADIUS_IN_METERS = 6378137

    PointPair getNewPointPair(Point origin, double distanceInMeters, double azimuth) {
        double angle = azimuth * Math.PI / 180.0
        double dx = distanceInMeters * Math.sin(angle)
        double dy = distanceInMeters * Math.cos(angle)
        double newLat = origin.lat + (dy / EARTH_RADIUS_IN_METERS) * (180 / Math.PI)
        double newLng = origin.lng + (dx / EARTH_RADIUS_IN_METERS) * (180 / Math.PI) / Math.cos(origin.lat * Math.PI / 180)
        Point destination = new Point(newLat, newLng)
        new PointPair(origin, destination, distanceInMeters, azimuth)
    }
}
