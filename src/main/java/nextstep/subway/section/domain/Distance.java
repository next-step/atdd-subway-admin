package nextstep.subway.section.domain;

import nextstep.subway.common.message.ExceptionMessage;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final int MAX_INVALID_DISTANCE = 0;

    @Column(name = "distance")
    private int distance;

    protected Distance() {
    }

    private Distance(int distance) {
        this.distance = distance;
    }

    public static Distance from(int distance) {
        validateDistance(distance);
        return new Distance(distance);
    }

    private static void validateDistance(int distance) {
        if (distance <= MAX_INVALID_DISTANCE) {
            throw new IllegalArgumentException(
                    String.format(ExceptionMessage.INVALID_SECTION_DISTANCE, distance)
            );
        }
    }

    public Distance subtract(Distance distance) {
        return Distance.from(this.distance - distance.distance);
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
