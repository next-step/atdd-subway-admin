package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(nullable = false)
    private long distance;

    protected Distance() {
    }

    public Distance(long distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("거리는 0보다 이상이어야 합니다.");
        }
        this.distance = distance;
    }

    public long minus(Distance distance) {
        return this.distance - distance.distance;
    }

    public boolean isLessThanEqual(Distance distance) {
        return this.distance <= distance.distance;
    }
}
