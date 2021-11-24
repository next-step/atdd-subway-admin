package nextstep.subway.line.domain;

import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private int distance;

    protected Distance() {
    }

    public Distance(final int distance) {
        verifyDistance(distance);

        this.distance = distance;
    }

    private void verifyDistance(final int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("거리는 음수가 올 수 없습니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Distance)) {
            return false;
        }
        final Distance that = (Distance)o;
        return this.distance == that.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.distance);
    }
}
