package nextstep.subway.line.domain;

import static nextstep.subway.common.Message.*;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import nextstep.subway.common.Message;

@Embeddable
public class Distance {



    @Column
    private int distance = 0;

    public Distance(int distance) {
        if (distance < 0) {
            throw new ArithmeticException(Message.format(MESSAGE_SECTION_DISTANCE_NOT_LESS_THAN_ZERO, distance));
        }
        this.distance = distance;
    }

    protected Distance() {
    }

    public Distance subtract(final Distance subtract) {
        final int result = distance - subtract.distance;
        return new Distance(result);
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Distance distance1 = (Distance)o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
