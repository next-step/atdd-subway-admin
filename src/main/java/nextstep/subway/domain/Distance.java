package nextstep.subway.domain;

import nextstep.subway.application.exception.exception.NotValidDataException;
import nextstep.subway.application.exception.type.ValidExceptionType;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final int SIZE_ZERO = 0;

    @Column(nullable = false)
    private int distance;

    protected Distance() {}

    public Distance(int distance) {
        validCheckDistance(distance);
        this.distance = distance;
    }

    private void validCheckDistance(int distance) {
        if (distance <= SIZE_ZERO) {
            throw new NotValidDataException(ValidExceptionType.NOT_VALID_DISTANCE.getMessage());
        }
    }

    public Distance minus(Distance distance) {
        return new Distance(this.distance - distance.distance);
    }

    public int getDistance() {
        return distance;
    }
}
