package nextstep.subway.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateLength(distance);
        this.distance = distance;
    }

    public void validateLength(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 길이는 0보다 큰 값을 입력해주세요.");
        }
    }

    public boolean isGreaterEqual(int distance) {
        return this.distance <= distance;
    }

    public int getDistance() {
        return distance;
    }

    public void setMinusDistance(int distance) {
        this.distance = getMinusDistance(distance);
    }

    private int getMinusDistance(int distance) {
        return Math.abs(this.distance - distance);
    }
}
