package nextstep.subway.domain;

import nextstep.subway.exception.SectionLengthOverException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MIN = 0;
    @Column
    private int distance;

    protected Distance() {
    }

    private Distance(int distance) {
        this.distance = distance;
    }

    public static Distance from(int distance) {
        return new Distance(distance);
    }

    public int getDistance() {
        return distance;
    }

    public void minus(Distance distance) {
        if (this.distance - distance.getDistance() <= MIN) {
            throw new SectionLengthOverException("기존 역 사이 거리보다 작을 수 없습니다.");
        }
        this.distance -= distance.getDistance();
    }
}
