package nextstep.subway.domain;

import nextstep.subway.consts.ErrorMessage;

public class Distance {
    private int distance;

    public static final int MINIMUM_DISTANCE = 1;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance < MINIMUM_DISTANCE) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessage.ERROR_DISTANCE_TOO_SMALL, MINIMUM_DISTANCE)
            );
        }
    }

    public Distance subtract(Distance distance) {
        return new Distance(this.distance - distance.getDistance());
    }

    public int getDistance() {
        return distance;
    }
}
