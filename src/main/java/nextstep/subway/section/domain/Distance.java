package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final int MIN_DISTANCE = 1;
    private static final String GREATER_THEN_MIN_DISTANCE = "지하철 구간은 최소 1 이상이어야 합니다.";

    @Column(nullable = false)
    private int distance;

    protected Distance() {}

    private Distance(int distance) {
        this.distance = distance;
    }

    public static Distance from(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(GREATER_THEN_MIN_DISTANCE);
        }
        return new Distance(distance);
    }

    public void decrease(Distance distance) {
        this.distance -= distance.distance;
    }

    public int get() {
        return this.distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
