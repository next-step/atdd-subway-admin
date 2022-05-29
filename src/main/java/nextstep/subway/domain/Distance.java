package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    @Column
    private Integer distance;

    protected Distance() {
    }

    public Distance(Integer distance) {
        validate(distance);
        this.distance = distance;
    }

    public static Distance of(Integer distance) {
        return new Distance(distance);
    }

    public Integer getDistance() {
        return distance;
    }

    private void validate(Integer distance) {
        if(null == distance || distance <= 0) {
            throw new IllegalArgumentException("구간길이는 0보다 커야합니다.");
        }
    }

    public void minus(Distance other) {
        if (this.distance <= other.distance) {
            throw new IllegalArgumentException("구간길이가 이전구간길이 보다 클수가 없습니다.");
        }
        this.distance -= other.distance;
    }

    public int plus(Distance other) {
        return this.distance += other.distance;
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

    @Override
    public String toString() {
        return "Distance{" +
                "distance=" + distance +
                '}';
    }
}
