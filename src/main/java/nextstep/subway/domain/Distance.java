package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column
    private Long distance;

    protected Distance() {
    }

    private Distance(Long distance) {
        validateInvalidValue(distance);
        this.distance = distance;
    }

    public static Distance of(Long distance) {
        return new Distance(distance);
    }

    private static void validateInvalidValue(Long distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 0 이하 일 순 없습니다.");
        }
    }

    public static Distance add(Distance distance1, Distance distance2) {
        return new Distance(distance1.distance + distance2.distance);
    }

    public static Distance subtract(Distance distance1, Distance distance2) {
        return new Distance(distance1.distance - distance2.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance1 = (Distance) o;
        return Objects.equals(distance, distance1.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
