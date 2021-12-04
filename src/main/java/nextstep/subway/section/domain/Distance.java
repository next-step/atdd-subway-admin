package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final int MIN_DISTANCE = 0;
    private static final String TOO_LOW_DISTANCE = "거리는 0보다 커야 합니다.";
    private static final String SHOULD_NEED_BIGGER_VALUE = "기존 구간보다 큰 수를 입력할 수 없습니다.";

    @Column
    private int distance;

    public Distance() {
    }

    public Distance(final int distance) {
        validate(distance, TOO_LOW_DISTANCE);
        this.distance = distance;
    }

    public void deduct(final int value) {
        int result = this.distance - value;
        validate(result, SHOULD_NEED_BIGGER_VALUE);
        this.distance = result;
    }

    private void validate(final int distance, final String errorMessage) {
        if (MIN_DISTANCE >= distance) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public int getDistance() {
        return distance;
    }

}
