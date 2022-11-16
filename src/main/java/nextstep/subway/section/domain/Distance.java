package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.common.exception.ErrorMessageConstant;

@Embeddable
public class Distance {
    private static final int ZERO = 0;

    @Column(nullable = false)
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    private void validate(int distance) {
       if(distance <= ZERO) {
            throw new IllegalArgumentException(ErrorMessageConstant.VALID_LINE_LENGTH_GREATER_THAN_ZERO);
        }
    }

    public void validNewDistance(int newDistance) {
        if(distance <= newDistance){
            throw new IllegalArgumentException(ErrorMessageConstant.VALID_GREATER_OR_EQUAL_LENGTH_BETWEEN_STATION);
        }
    }

    public Distance subtract(Distance newDistance) {
        return new Distance(distance - newDistance.distance);
    }

    public Distance add(Distance another) {
        return new Distance(distance + another.distance);
    }

    public int get() {
        return distance;
    }
}
