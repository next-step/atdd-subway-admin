package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final int MIN_VALUE = 1;
    private static final String MINIMUM_VALUE_INVALID = "역 간 거리는 %d 이상이어야 합니다.";

    @Column(name = "distance", nullable = false)
    private int distance;

    protected Distance() {}

    private Distance(final int distance) {
        this.distance = distance;
    }

    public static Distance from(final int distance) {
        validateMinimumValue(distance);
        return new Distance(distance);
    }

    private static void validateMinimumValue(final int distance) {
        if (MIN_VALUE > distance) {
            throw new IllegalArgumentException(String.format(MINIMUM_VALUE_INVALID, MIN_VALUE));
        }
    }

    public Distance distanceDiffWithOtherDistance(Distance other) {
        int distanceDiff = this.distance - other.getDistance();
        return Distance.from(distanceDiff);
    }

    public Distance plus(int otherDistance) {
        return Distance.from(this.distance + otherDistance);
    }

    public int getDistance() {
        return distance;
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
