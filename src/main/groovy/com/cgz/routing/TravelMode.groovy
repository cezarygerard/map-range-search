package com.cgz.routing

/**
 * Created by czarek on 17.02.17.
 */
enum TravelMode {
    DRIVING("driving"),
    WALKING("walking"),
    BICYCLING("bicycling");

    private final String value;

    private TravelMode(String value) {
        this.value = value
    }

    static Optional<TravelMode> fromString(String text) {
        for (TravelMode mode : TravelMode.values()) {
            if (mode.value.equalsIgnoreCase(text)) {
                return Optional.of(mode);
            }
        }
        return Optional.empty();
    }

    String getValue() {
        return value
    }
}