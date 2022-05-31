package nextstep.subway.line.domain;

import static nextstep.subway.common.exception.ErrorMessage.OVER_SIZED_DISTANCE_ERROR;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance implements Comparable<Distance> {
    private static final int ZERO_VALUE = 0;

    @Column
    private int distance;

    protected Distance() {}

    private Distance(int distance) {
        this.distance = distance;
    }

    public static Distance from(int distance) {
        return new Distance(distance);
    }

    public void subtract(Distance distanceObject) {
        if (this.compareTo(distanceObject) <= ZERO_VALUE) {
            throw new IllegalArgumentException(OVER_SIZED_DISTANCE_ERROR.getMessage());
        }
        this.distance -= distanceObject.distance;
    }

    public void add(Distance distanceObject) {
        this.distance += distanceObject.distance;
    }

    @Override
    public int compareTo(Distance distanceObject) {
        return this.distance - distanceObject.distance;
    }
}
