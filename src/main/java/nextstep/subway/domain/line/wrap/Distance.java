package nextstep.subway.domain.line.wrap;

import java.util.Objects;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

@Embeddable
@Access(AccessType.FIELD)
public class Distance {

    public static final int MIN = 0;
    public static final String INVALID_DISTANCE_ERROR_MESSAGE = "구간 거리는 0 보다 큰 값을 입력하세요";

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance <= MIN) {
            throw new IllegalArgumentException(INVALID_DISTANCE_ERROR_MESSAGE);
        }
    }

    public boolean isOverOrEquals(int targetDistance) {
        return distance <= targetDistance;
    }

    public int getDistance() {
        return distance;
    }

    public void updateDistance(int distance) {
        this.distance = this.distance - distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
