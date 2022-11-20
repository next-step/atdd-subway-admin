package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    public static final String MESSAGE_DISTANCE_SHOULD_BE_POSITIVE = "거리는 항상 0보다 커야 합니다";
    @Column(nullable = false)
    private Long distance;

    protected Distance() {
    }

    private Distance(Long distance) {
        this.distance = distance;
    }

    public static Distance of(Long distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException(MESSAGE_DISTANCE_SHOULD_BE_POSITIVE);
        }
        return new Distance(distance);
    }

    public Distance plus(Long distance) {
        return Distance.of(this.distance + distance);
    }

    public Distance minus(long distance) {
        return Distance.of(this.distance - distance);
    }

    public long get(){
        return this.distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return Objects.equals(distance, distance1.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
