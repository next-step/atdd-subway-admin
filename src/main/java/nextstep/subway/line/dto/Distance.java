package nextstep.subway.line.dto;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.Objects;

/**
 * packageName : nextstep.subway.line.dto
 * fileName : Distance
 * author : haedoang
 * date : 2021/11/21
 * description :
 */
@Embeddable
public class Distance {
    @Transient
    public static final int MIN_DISTANCE = 1;

    private int distance;

    private Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    protected Distance() {
    }

    public static Distance of(int distance) {
        return new Distance(distance);
    }

    private void validate(int distance) {
        if (distance < MIN_DISTANCE) throw new IllegalArgumentException("거리는 1보다 작을 수 없습니다.");
    }

    public int intValue() {
        return this.distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

    public boolean isBiggerThan(Distance distance) {
        return this.distance > distance.intValue();
    }

    public void minus(Distance distance) {
        this.distance -= distance.intValue();
    }

    public void plus(Distance distance) {
        this.distance += distance.intValue();
    }
}
