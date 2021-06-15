package nextstep.subway.line.domain.wrappers;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    public static final String NEGATIVE_NUMBER_ERROR_MESSAGE = "구간거리는 값은 음수가 입력할 수 없습니다.";
    private static final String OUT_BOUND_DISTANCE_ERROR_MESSAGE = "구간 사이에 새로운 역을 등록 시 구간거리는 기존 등록된 구간 거리보다 작아야합니다.";
    private int distance;

    public Distance() {
    }

    public Distance(int distance) {
        checkValidNegative(distance);
        this.distance = distance;
    }

    public Distance subtractionDistance(Distance other) {
        int value = distance - other.distance;
        checkValidZeroOrNegative(value, OUT_BOUND_DISTANCE_ERROR_MESSAGE);
        return new Distance(value);
    }

    private void checkValidNegative(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException(NEGATIVE_NUMBER_ERROR_MESSAGE);
        }
    }

    private void checkValidZeroOrNegative(int distance, String message) {
        if (distance <= 0) {
            throw new IllegalArgumentException(message);
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
