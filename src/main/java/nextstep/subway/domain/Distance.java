package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(name = "distance")
    private int distance;

    public Distance() {
    }

    public Distance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리 정보는 양수로 입력해야 합니다.");
        }
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }
}
