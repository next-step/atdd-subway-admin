package nextstep.subway.line.domain;

import java.security.InvalidParameterException;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column
    private Integer distance;

    protected Distance() {
    }

    protected Distance(Integer distance) {
        this.distance = distance;
    }

    public static Distance of(Integer distance) {
        return new Distance(distance);
    }

    public Integer getDistance() {
        return distance;
    }

    public void minus(Distance distance) {
        if (this.distance <= distance.distance) {
            throw new InvalidParameterException("추가역은 기존 구간길이 보다 미만이어야 합니다.");
        }

        this.distance -= distance.distance;
    }

    public void plus(Distance distance) {
        this.distance += distance.distance;
    }

    public void zero() {
        this.distance = 0;
    }
}
