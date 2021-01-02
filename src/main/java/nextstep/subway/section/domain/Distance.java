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
            this.distance -= newDistance;
        }
        this.distance = newDistance;
    }

}
