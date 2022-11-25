package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(name = "distance")
    private Long distance;

    protected Distance() {}

    private Distance(Long distance) {
        this.distance = distance;
    }

    public static Distance from(Long distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("역 사이의 거리는 0이하일 수 없습니다.");
        }
        return new Distance(distance);
    }

    public Distance subtract(Distance distance) {
        if (this.distance <= distance.distance) {
            throw new IllegalArgumentException("새로운 역이 기존 역 사이보다 크거나 같으면 등록할 수 없다");
        }
        return Distance.from(this.distance -= distance.distance);
    }

    public Distance add(Distance distance) {
        return Distance.from(this.distance + distance.getDistance());
    }

    public Long getDistance() {
        return distance;
    }
}
