package com.cgz.routing

/**
 * Created by czarek on 17.02.17.
 */
enum TravelMode {
    DRIVING("driving", DRIVING_SPEED_METERS_PER_MINUTE),
    WALKING("walking", WALKING_SPEED_METERS_PER_MINUTE),
    BICYCLING("bicycling", BIKING_SPEED_METERS_PER_MINUTE);


    private static final double DRIVING_SPEED_METERS_PER_MINUTE = 700D

    private static final double BIKING_SPEED_METERS_PER_MINUTE = 225D

    private static final double WALKING_SPEED_METERS_PER_MINUTE = 70D

    private final double defaultSpeedPerMinute;

    private final String name;

    private TravelMode(String name, double defaultSpeedPerMinute) {
        this.name = name
        this.defaultSpeedPerMinute = defaultSpeedPerMinute
    }

    static Optional<TravelMode> fromString(String text) {
        for (TravelMode mode : TravelMode.values()) {
            if (mode.name.equalsIgnoreCase(text)) {
                return Optional.of(mode);
            }
        }
        return Optional.empty();
    }

    String getName() {
        return name
    }

    double getDefaultSpeedPerMinute() {
        return defaultSpeedPerMinute
    }
}