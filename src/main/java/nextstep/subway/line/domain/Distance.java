package nextstep.subway.line.domain;

import nextstep.subway.Exception.CannotUpdateSectionException;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        this.distance = distance;
    }

    public boolean isGreaterThan(int distance) {
        if (this.distance <= distance) {
            throw new CannotUpdateSectionException("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
        }
        return true;
    }

    public void minus(int distance) {
        this.distance -= distance;
    }

    public int getDistance() {
        return distance;
    }
}
