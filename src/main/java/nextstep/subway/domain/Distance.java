package nextstep.subway.domain;

import nextstep.subway.exception.InvalidDistanceException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final Long MIN = 1L;
    @Column
    private Long distance;

    protected Distance() {
    }

    public Distance(Long distance) {
        validate(distance);
        this.distance = distance;
    }

    private void validate(Long distance) {
        if (distance < MIN) {
            throw new InvalidDistanceException(MIN);
        }
    }

    public Distance minus(Distance target) {
        return new Distance(distance - target.distance);
    }

    public Long getDistance() {
        return distance;
    }

    public boolean isLong(Distance target) {
        return distance > target.distance;
    }

    public Distance plus(Distance target) {
        return new Distance(distance + target.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Distance that = (Distance) o;

        return distance.equals(that.distance);
    }
}
