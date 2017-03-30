package com.cgz.routing.googlemaps

import com.cgz.geomath.Point
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import groovy.transform.PackageScope
import org.springframework.stereotype.Service

import java.math.RoundingMode
import java.util.concurrent.atomic.AtomicInteger

//TODO test it first!!
@Service
class RouteCache {

    AtomicInteger getCounter = new AtomicInteger(0);
    AtomicInteger putCounter = new AtomicInteger(0);

    static final int PRECISION = 4
    //TODO make sure cache takes TRAVEL MODE into account

    //TODO replace with configurable values
    private Cache<CacheKey, Long> cache = CacheBuilder.newBuilder().initialCapacity(10000).maximumSize(1_000_000).build();

    Optional<Long> get(Point origin, Point destination) {
        CacheKey cacheKey = new CacheKey(origin, destination)
        Long value = cache.getIfPresent(cacheKey)

        if (value != null) {
            def val = getCounter.incrementAndGet()
        }

        return Optional.ofNullable(value)
    }

    void put(Point origin, Point destination, long time) {
        CacheKey cacheKey = new CacheKey(origin, destination)
        cache.put(cacheKey, time)
        putCounter.incrementAndGet()
    }

    @PackageScope
    class CacheKey {
        BigDecimal originLat
        BigDecimal originLng
        BigDecimal destinationLat
        BigDecimal destinationLng

        CacheKey(Point origin, Point destination) {
            this.originLat = new BigDecimal(origin.lat).setScale(PRECISION, RoundingMode.HALF_UP)
            this.originLng = new BigDecimal(origin.lng).setScale(PRECISION, RoundingMode.HALF_UP)
            this.destinationLat = new BigDecimal(destination.lat).setScale(PRECISION, RoundingMode.HALF_UP)
            this.destinationLng = new BigDecimal(destination.lng).setScale(PRECISION, RoundingMode.HALF_UP)
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            CacheKey cacheKey = (CacheKey) o

            if (!destinationLat.equals(cacheKey.destinationLat)) return false
            if (!destinationLng.equals(cacheKey.destinationLng)) return false
            if (!originLat.equals(cacheKey.originLat)) return false
            if (!originLng.equals(cacheKey.originLng)) return false

            return true
        }

        int hashCode() {
            int result
            result = (originLat != null ? originLat.hashCode() : 0)
            result = 31 * result + (originLng != null ? originLng.hashCode() : 0)
            result = 31 * result + (destinationLat != null ? destinationLat.hashCode() : 0)
            result = 31 * result + (destinationLng != null ? destinationLng.hashCode() : 0)
            return result
        }
    }
}

