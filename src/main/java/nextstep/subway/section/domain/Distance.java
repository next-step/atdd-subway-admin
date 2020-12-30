package nextstep.subway.section.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Distance {
    private static final String INVALID_VALUE_ERROR_MESSAGE = "거리는 0보다 커야 합니다.";
    private static final int ZERO = 0;
    private int distance;

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if(distance <= ZERO) {
            throw new IllegalArgumentException(INVALID_VALUE_ERROR_MESSAGE);
        }
    }
}
