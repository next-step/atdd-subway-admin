package nextstep.subway.domain;

public class Distance {
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("Distance는 최소 1 이상이어야 합니다.");
        }
    }

    public Distance subtract(Distance distance) {
        return new Distance(this.distance - distance.getDistance());
    }

    public int getDistance() {
        return distance;
    }
}
