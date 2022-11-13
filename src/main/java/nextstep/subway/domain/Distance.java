package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.constant.ErrorCode;

@Embeddable
public class Distance {

    private static final Long ZERO = 0L;

    @Column(nullable = false)
    private Long distance;

    protected Distance() {
    }

    private Distance(Long distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    public static Distance from(Long distance) {
        return new Distance(distance);
    }

    private void validateDistance(Long distance) {
        if(distance == null) {
            throw new IllegalArgumentException(ErrorCode.노선거리는_비어있을_수_없음.getErrorMessage());
        }
        if(distance <= ZERO) {
            throw new IllegalArgumentException(ErrorCode.노선거리는_0보다_작거나_같을_수_없음.getErrorMessage());
        }
    }

    public Distance subtract(Distance distance) {
        return new Distance(this.distance - distance.distance);
    }

    public Distance add(Distance distance) {
        return new Distance(this.distance + distance.distance);
    }

    public Long value() {
        return distance;
    }
}
