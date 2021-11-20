package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final int MIN_DISTANCE = 0;

    @Column
    private int distance;

    public Distance() {
    }

    public Distance(final int distance) {
        validate(distance);
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    private void validate(int distance) {
        if (MIN_DISTANCE >= distance) {
            throw new IllegalArgumentException("거리는 0보다 커야 합니다.");
        }
    }

}
