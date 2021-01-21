package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(nullable = false)
    int distance;

    public Distance() {
    }

    public Distance(int distance) {
        if(distance < 1) {
            throw new IllegalArgumentException("거리는 0보다 이상이어야 합니다.");
        }
        this.distance = distance;
    }

    public int minus(Distance distance) {
        return this.distance - distance.distance;
    }

    public boolean isLessThanEqual(Distance distance) {
        return this.distance <= distance.distance;
    }
}
