package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(nullable = false)
    private Integer distance;

    protected Distance() {
    }

    protected Distance(Integer distance) {
        validDistance(distance);
        this.distance = distance;
    }

    private void validDistance(Integer distance) {
        if (Objects.isNull(distance)) {
            throw new IllegalArgumentException();
        }

        if (distance <= 0) {
            throw new IllegalArgumentException();
        }
    }

    public static Distance from(Integer distance) {
        return new Distance(distance);
    }

    public Integer getDistance() {
        return distance;
    }
}
