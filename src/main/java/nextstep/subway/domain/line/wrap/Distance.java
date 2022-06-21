package nextstep.subway.domain.line.wrap;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

@Embeddable
@Access(AccessType.FIELD)
public class Distance {

    public static final String INVALID_DISTANCE_ERROR_MESSAGE = "구간 거리는 0 보다 큰 값을 입력하세요";

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
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
}
