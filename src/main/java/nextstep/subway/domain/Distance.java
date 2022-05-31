package nextstep.subway.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final Long ZERO = 0L;
    private final Long distance;

    protected Distance() {
        this(0L);
    }

    public Distance(final Long distance) {
        if (ZERO > distance) {
            throw new IllegalArgumentException("invalid parameter");
        }
        this.distance = distance;
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
