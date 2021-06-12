package nextstep.subway.section.domain.wrapper;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private int distance;

    public Distance() {
    }

    public Distance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Distance distance1 = (Distance) object;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
