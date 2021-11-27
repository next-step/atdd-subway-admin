package nextstep.subway.section.domain;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column
    private int distance;

    public Distance() {
        distance = 0;
    }

    public Distance(int distance) {
        checkValud(distance);
        this.distance = distance;
    }

    private void checkValud(int distance) {
        if (distance <= 0) {
            throw new InputDataErrorException(InputDataErrorCode.DISTANCE_IS_NOT_LESS_THEN_ZERO);
        }
    }

    public Distance minus(Distance distance) {
        return new Distance(this.distance - distance.distance);
    }

    public Distance sum(Distance distance) {
        return new Distance(this.distance + distance.distance);
    }

    public int getDistance() {
        return this.distance;
    }

}
