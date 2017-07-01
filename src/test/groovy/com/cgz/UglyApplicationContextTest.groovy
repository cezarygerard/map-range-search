package com.cgz

import spock.lang.Specification

class UglyApplicationContextTest extends Specification {

    def "Creates instances"() {
        when:
        UglyApplicationContext context = UglyApplicationContext.getInstance()
        then:
        context.mapController.class == MapController.class

    }
}
