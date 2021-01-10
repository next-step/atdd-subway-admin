package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(nullable = false)
    private Long distance;

    protected Distance() {
    }

    public Distance(long distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("거리 `distance`는 0보다 작을 수 없습니다.");
        }
        this.distance = distance;
    }

    public boolean isLessThanEqual(Distance distance) {
        return this.distance <= distance.distance;
    }

    public long minus(Distance distance) {
        return this.distance - distance.distance;
    }
}
