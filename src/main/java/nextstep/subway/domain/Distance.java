package nextstep.subway.domain;

import javax.persistence.Embeddable;

import static nextstep.subway.exception.ErrorMessage.DISTANCE_BETWEEN_STATION_OVER;
import static nextstep.subway.exception.ErrorMessage.DISTANCE_CANNOT_BE_ZERO;

@Embeddable
public class Distance {

    private int distance;

    protected Distance() {

    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException(DISTANCE_CANNOT_BE_ZERO.getMessage());
        }
    }


    public void minus(Distance distance) {
        if (this.distance <= distance.getDistance()) {
            throw new IllegalArgumentException(DISTANCE_BETWEEN_STATION_OVER.getMessage());
        }
        this.distance -= distance.getDistance();
    }

    public void plus(Distance distance) {
        this.distance += distance.getDistance();
    }

    public int getDistance() {
        return distance;
    }
}
