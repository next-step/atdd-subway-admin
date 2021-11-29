package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    public static final String MESSAGE_SECTION_DISTANCE_NOT_LESS_THAN_ZERO = "구간의 거리가 0과 같거나 작을 수 없습니다. 입력된 값[%s]";

    @Column
    private int distance = 0;

    public Distance(int distance) {
        if (distance < 0) {
            throw new ArithmeticException(String.format(MESSAGE_SECTION_DISTANCE_NOT_LESS_THAN_ZERO, distance));
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
