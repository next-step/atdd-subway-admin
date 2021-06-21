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

    public int getDistance() {
        return distance;
    }

}
