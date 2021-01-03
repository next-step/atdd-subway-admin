package nextstep.subway.section.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Distance {
    private static final String NEGATIVE_VALUE_ERROR_MESSAGE = "거리는 0보다 커야 합니다.";
    private static final String INVALID_DISTANCE_ERROR_MESSAGE = "구간을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.";
    private static final int ZERO = 0;
    private int distance;

    public Distance(int distance) {
        validateInitDistance(distance);
        this.distance = distance;
    }

    private void validateInitDistance(int distance) {
        if(distance < ZERO) {
            throw new IllegalArgumentException(NEGATIVE_VALUE_ERROR_MESSAGE);
        }
    }

    public void updateDistance(int newDistance) {
        if (this.distance != 0) {
            validateDistance(newDistance);
            this.distance -= newDistance;
        }
        this.distance = newDistance;
    }

    private void validateDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException(INVALID_DISTANCE_ERROR_MESSAGE);
        }
    }

}
