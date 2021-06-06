package nextstep.subway.section.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final Long MINIMUM_NUMBER = 0L;

    private Long distance;

    protected Distance() {
        this(MINIMUM_NUMBER);
    }

    public Distance(Long distance) {
        if (distance < MINIMUM_NUMBER) {
            throw new IllegalArgumentException("거리는 음수일 수 없습니다.");
        }

        this.distance = distance;
    }

    public Distance minus(Distance distance) {
        return new Distance(this.distance - distance.distance);
    }

    public Distance plus(Distance distance) {
        return new Distance(this.distance + distance.distance);
    }

    public boolean isMoreThan(Distance distance) {
        return this.distance >= distance.distance;
    }

    public boolean isLessThan(Distance distance) {
        return this.distance <= distance.distance;
    }

    public Long toLong() {
        return distance;
    }

}
