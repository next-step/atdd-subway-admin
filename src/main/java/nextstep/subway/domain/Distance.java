package nextstep.subway.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        valiPositives(distance);
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean equalsOrGreaterThan(Distance distance) {
        return this.distance >= distance.distance;
    }

    private void valiPositives(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("distance는 0보다 커야합니다. 입력값:" + distance);
        }
    }

    public Distance minus(Distance distance) {
        if (this.distance <= distance.getDistance()) {
            throw new IllegalArgumentException("구간 길이가 같거나 기존의 구간이 더 작습니다. 입력값:" + distance.getDistance());
        }
        return new Distance(this.distance - distance.getDistance());
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
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

    public Distance merge(Distance distance) {
        return new Distance(this.distance + distance.getDistance());
    }
}
