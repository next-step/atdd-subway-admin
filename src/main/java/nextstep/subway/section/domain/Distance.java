package nextstep.subway.section.domain;

import java.util.Objects;
import javax.persistence.Embeddable;
import nextstep.subway.line.common.Constants;

@Embeddable
public class Distance {
    private Integer distance;

    protected Distance() {
    }

    private Distance(Integer distance) {
        this.distance = distance;
    }

    public static Distance createDownDistance() {
        return new Distance(Constants.DOWN_DISTANCE);
    }

    public static Distance from(Integer distance) {
        return new Distance(distance);
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
