package nextstep.subway.domain;

import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final String DISTANCE_OVER_ERROR_MESSAGE = "역 사이의 거리가 1보다 작을 수는 없습니다.";

    private long distance;

    protected Distance() {
    }

    private Distance(long distance) {
        validate(distance);
        this.distance = distance;
    }

    private void validate(long distance) {
        if (distance < 1) {
            throw new IllegalArgumentException(DISTANCE_OVER_ERROR_MESSAGE);
        }
    }

    public static Distance of(long distance) {
        return new Distance(distance);
    }


    public Distance sum(Distance distance) {
        return new Distance(this.distance + distance.distance);
    }

    public Distance sub(Distance distance) {
        return new Distance(this.distance - distance.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Distance distance1 = (Distance)o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
