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

    public Distance(Integer distance) {
        validate(distance);
        this.distance = distance;
    }

    public Distance add(Distance distance) {
        return new Distance(Math.addExact(this.distance, distance.get()));
    }

    public Distance subtract(Distance distance) {
        if (this.distance <= distance.get()) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
        return new Distance(Math.subtractExact(this.distance, distance.get()));
    }

    private void validate(Integer distance) {
        if(distance == null || distance < 1) {
            throw new IllegalArgumentException("길이는 1 이상이어야 합니다.");
        }
    }

    public Integer get() {
        return distance;
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
        return distance != null ? distance.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "distance=" + distance +
                '}';
    }
}
