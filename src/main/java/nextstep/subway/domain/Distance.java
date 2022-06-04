package nextstep.subway.domain;

import static nextstep.subway.message.ErrorMessage.DISTANCE_VALUE_IS_MIN_ONE;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MIN_DISTANCE = 0;
    private static final IllegalArgumentException DISTANCE_VALID_EXCEPTION
            = new IllegalArgumentException(DISTANCE_VALUE_IS_MIN_ONE.toMessage());

    @Column
    private int distance;

    protected Distance() {

    }

    private Distance(int distance) {
        valid(distance);
        this.distance = distance;
    }

    public static Distance of(int distance) {
        return new Distance(distance);
    }

    public boolean isLess(Distance compareSource) {
        return this.value() < compareSource.value();
    }

    public void minusDistance(int number) {
        valid(this.distance - number);
        this.distance -= number;
    }

    public void plusDistance(int number) {
        valid(this.distance + number);
        this.distance += number;
    }

    private void valid(int distance) {
        if (MIN_DISTANCE >= distance) {
            throw DISTANCE_VALID_EXCEPTION;
        }
    }

    public int value() {
        return distance;
    }
}
