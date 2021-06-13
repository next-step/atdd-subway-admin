package nextstep.subway.wrappers;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    public static final String NEGATIVE_NUMBER_ERROR_MESSAGE = "구간거리는 값은 음수가 입력할 수 없습니다.";
    private int distance;

    public Distance() {
    }

    public Distance(int distance) {
        checkValidNegative(distance);
        this.distance = distance;
    }

    public Distance subtractionDistance(Distance other) {
        return new Distance(distance - other.distance);
    }

    public int distance() {
        return distance;
    }

    private void checkValidNegative(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException(NEGATIVE_NUMBER_ERROR_MESSAGE);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Distance distance1 = (Distance) object;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
