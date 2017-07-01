package com.cgz

import com.cgz.geomath.Point

enum Location {
    WARSAW(52.2297, 21.0122),
    PARIS(48.85341, 2.3488000)

    public static final WARSAW_POINT = WARSAW.asPoint()
    public static final PARIS_POINT = PARIS.asPoint()

    private final Point centerPoint

    Location(final double lat, final double lng) {
        this.centerPoint = new Point(lat, lng)
    }

    Point asPoint() {
        new Point(centerPoint.lat, centerPoint.lng)
    }

    double getLat() {
        return centerPoint.lat
    }

    double getLng() {
        return centerPoint.lng
    }
}

