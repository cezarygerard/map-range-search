package com.cgz.routing.googlemaps

import com.cgz.geomath.Point
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader

/**
 * Created by czarek on 28.02.17.
 */
class CacheManager {

    Cache<CacheKey,Long> cache = CacheBuilder.newBuilder().initialCapacity(999).maximumSize(9999).build();

   
    CacheLoader<CacheKey, Long> cacheLoader = new CacheLoader<CacheKey, Long>() {
        @Override
        Long load(CacheKey key) throws Exception {

        }
    }


    class CacheKey {
        Point origin;
        Point destination;

        CacheKey(Point origin, Point destination, long time) {
            this.origin = origin
            this.destination = destination
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            CacheKey entry = (CacheKey) o

            if (! destination.equals(entry.destination)) return false
            if (! origin.equals(entry.origin)) return false

            return true
        }

        int hashCode() {
            int result
            result = (origin != null ? origin.hashCode() : 0)
            result = 31 * result + (destination != null ? destination.hashCode() : 0)
            return result
        }
    }
}

