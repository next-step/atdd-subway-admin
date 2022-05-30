package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance implements Comparable<Distance> {
    private static final String OVER_SIZED_DISTANCE_ERROR_MESSAGE = "기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.";

    @Column
    private int distance;

    protected Distance() {}

    private Distance(int distance) {
        this.distance = distance;
    }

    public static Distance from(int distance) {
        return new Distance(distance);
    }

    public void subtract(Distance distance) {
        if (this.compareTo(distance) <= 0) {
            throw new IllegalArgumentException(OVER_SIZED_DISTANCE_ERROR_MESSAGE);
        }
        this.distance -= distance.getDistance();
    }

    @Override
    public int compareTo(Distance distance) {
        return this.distance - distance.getDistance();
    }

    public int getDistance() {
        return distance;
    }
}
