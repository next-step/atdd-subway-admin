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
        this.distance = distance;
    }

    public void minus(Distance distance) {
        this.distance -= distance.distance;
        if (this.distance <= 0) {
            throw new IllegalArgumentException("등록할 구간의 길이가 기존 역 사이 길이보다 크거나 같습니다. (입력값: " + distance.distance + ")");
        }
    }

    public int getDistance() {
        return distance;
    }
}
