package nextstep.subway.section.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    private void validate(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("잘못된 거리 입력입니다");
        }
    }

    public int getDistance() {
        return distance;
    }
}
