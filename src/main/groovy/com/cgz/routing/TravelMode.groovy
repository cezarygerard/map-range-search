package com.cgz.routing

/**
 * Created by czarek on 17.02.17.
 */
enum TravelMode {
    DRIVING("driving", DRIVING_SPEED_METERS_PH),
    WALKING("walking", WALKING_SPEED_METERS_PH),
    BICYCLING("bicycling", BIKING_SPEED_METERS_PH);


    private static double DRIVING_SPEED_METERS_PH = 700D

    private static double BIKING_SPEED_METERS_PH = 225D

    private static double WALKING_SPEED_METERS_PH = 70D

    private final double defaultSpeedPerMinute;

    private final String value;

    private TravelMode(String value, double defaultSpeedPerMinute) {
        this.value = value
        this.defaultSpeedPerMinute = defaultSpeedPerMinute
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

    double getDefaultSpeedPerMinute() {
        return defaultSpeedPerMinute
    }
}