package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MIN_DISTANCE = 1;
    private static final String ERROR_MESSAGE_MIN_DISTANCE = "구간 길이는 " + MIN_DISTANCE + "보다 커야 합니다.";
    private static final String ERROR_MESSAGE_NOT_VALID_DISTANCE = "새로 등록할 구간의 길이는 기존 구간의 길이보다 작아야 합니다.";

    @Column
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    private void validate(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(ERROR_MESSAGE_MIN_DISTANCE);
        }
    }

    public void setDistanceGap(Distance newDistance) {
        this.distance = minus(newDistance);
    }

    private int minus(Distance other) {
        if (!isLonger(other)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_VALID_DISTANCE);
        }
        return this.distance - other.distance;
    }

    private boolean isLonger(Distance distance) {
        return this.distance > distance.getDistance();
    }

    public int getDistance() {
        return this.distance;
    }
}
