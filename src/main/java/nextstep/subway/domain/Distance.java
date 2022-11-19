package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(name = "distance")
    private Long distance;

    protected Distance() {
    }

    public Distance(Long distance) {
        this.distance = distance;
    }

    public void sub(Distance distance) {
        if (this.distance <= distance.distance) {
            throw new IllegalArgumentException("새로운 역이 기존 역 사이보다 크거나 같으면 등록할 수 없다");
        }
        this.distance -= distance.distance;
    }
}
