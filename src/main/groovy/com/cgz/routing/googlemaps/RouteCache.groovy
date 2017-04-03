package com.cgz.routing.googlemaps

import com.cgz.geomath.Point
import com.cgz.routing.TravelMode
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import groovy.transform.EqualsAndHashCode
import groovy.transform.PackageScope
import groovy.transform.ToString
import org.springframework.stereotype.Service

import java.math.RoundingMode
import java.util.concurrent.atomic.AtomicInteger

//TODO test it first!!
@Service
class RouteCache {

    AtomicInteger getCounter = new AtomicInteger(0);
    AtomicInteger putCounter = new AtomicInteger(0);

    static final int PRECISION = 3

    //TODO replace with configurable values
    private Cache<CacheKey, Long> cache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(1_000_000).build();

    Optional<Long> get(Point origin, Point destination, TravelMode mode) {
        CacheKey cacheKey = new CacheKey(origin, destination, mode)
        Long value = cache.getIfPresent(cacheKey)

        if (value != null) {
            println "cache hit" + getCounter.incrementAndGet()
        }

        return Optional.ofNullable(value)
    }

    void put(Point origin, Point destination, long time, TravelMode mode) {
        CacheKey cacheKey = new CacheKey(origin, destination, mode)
        cache.put(cacheKey, time)
        putCounter.incrementAndGet()
    }

    @PackageScope
    @EqualsAndHashCode
    @ToString
    class CacheKey {
        BigDecimal originLat
        BigDecimal originLng
        BigDecimal destinationLat
        BigDecimal destinationLng
        TravelMode mode

        CacheKey(Point origin, Point destination, TravelMode mode) {
            this.originLat = new BigDecimal(origin.lat).setScale(PRECISION, RoundingMode.HALF_UP)
            this.originLng = new BigDecimal(origin.lng).setScale(PRECISION, RoundingMode.HALF_UP)
            this.destinationLat = new BigDecimal(destination.lat).setScale(PRECISION, RoundingMode.HALF_UP)
            this.destinationLng = new BigDecimal(destination.lng).setScale(PRECISION, RoundingMode.HALF_UP)
            this.mode = mode
        }
    }
}

