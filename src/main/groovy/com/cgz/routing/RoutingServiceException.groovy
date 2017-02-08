package com.cgz.routing

/**
 * Created by czarek on 08.02.17.
 */
class RoutingServiceException extends RuntimeException {
    RoutingServiceException(String message, Throwable e) {
        super(message, e)
    }
}
