package nextstep.subway.domain.line;

import nextstep.subway.message.LineMessage;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    @Column
    private final Integer distance;

    protected Distance() {
        this.distance = null;
    }

    public Distance(Integer distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if(distance < 1) {
            throw new IllegalArgumentException(LineMessage.ERROR_LINE_DISTANCE_MORE_THAN_ONE.message());
        }
    }

    public Distance minus(Distance other) {
        return new Distance(this.distance - other.distance);
    }

    public boolean isMoreThan(Distance other) {
        return this.distance >= other.distance;
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
        return distance;
    }

    @Override
    public String toString() {
        return String.valueOf(distance);
    }
}
