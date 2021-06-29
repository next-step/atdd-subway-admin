package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("최소 거리는 1 입니다.");
        }

        this.distance = distance;
    }

    public void subtract(Distance other) {
        int result = this.distance - other.distance;

        if (result <= 0) {
            throw new IllegalArgumentException("새로운 구간 거리가 너무 깁니다.");
        }

        this.distance = result;
    }

}
