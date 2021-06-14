package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    public static final String INVALID_DISTANCE_EXCEPTION_MESSAGE = "구간 거리는 0보다 커야합니다.";

    @Column
    private int distance;

    protected Distance() {
    }

    private Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    public static Distance valueOf(int distance){
        return new Distance(distance);
    }

    private void validate(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException(INVALID_DISTANCE_EXCEPTION_MESSAGE);
        }
    }

    public int getDistance() {
        return distance;
    }

    public Distance minus(Distance distanceToMinus) {
        return Distance.valueOf(distance - distanceToMinus.getDistance());
    }

    public boolean isAvailableMinus(Distance distanceToMinus){
        return distance > distanceToMinus.getDistance();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
