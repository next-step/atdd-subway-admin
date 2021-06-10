package nextstep.subway.section.domain;

public class Distance {
    private static final int MINIMUM_DISTANCE = 1;
    private int distance;

    public Distance() {
        this.distance = 0;
    }

    public int getDistance() {
        return distance;
    }

    public Distance addDistance(int distance) {
        this.distance += distance;
        if (this.distance < MINIMUM_DISTANCE) {
            throw new IllegalArgumentException("합산된 구간의 길이가 1보다 작습니다.");
        }
        return this;
    }

    public boolean isGreaterThan(int distance) {
        return this.distance > distance;
    }

    public boolean isEqualTo(int distance) {
        return this.distance == distance;
    }

    public boolean isGreaterThanOrEqualTo(int distance) {
        return this.distance >= distance;
    }
}
