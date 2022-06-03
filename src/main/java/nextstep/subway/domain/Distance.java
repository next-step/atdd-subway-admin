package nextstep.subway.domain;

import nextstep.subway.exception.SectionLengthOverException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MIN = 0;
    @Column
    private final int distance;

    public Distance() {
        distance = 0;
    }

    public Distance(int distance) {
        if (distance < MIN) {
            throw new IllegalArgumentException("거리는 0보다 작을 수 없습니다.");
        }
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public Distance minus(Distance distance) {
        if (this.distance - distance.getDistance() <= MIN) {
            throw new SectionLengthOverException("기존 역 사이 거리보다 작을 수 없습니다.");
        }
        return new Distance(this.distance - distance.getDistance());
    }
}
