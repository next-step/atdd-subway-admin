package nextstep.subway.section.dto;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        this.distance = distance;
    }

    /**
     * 두 거리를 비교하여 새로운 거리를 반환합니다.
     * 현재 거리를 기준으로 두 거리의 차이가 없거나 0보다 작으면 오류를 발생합니다.
     * @param distance
     * @return 두거리의 차이
     * @throws IllegalArgumentException
     */
    public Distance difference(Distance distance) {
        int difference = this.getDistance() - distance.distance;
        if(difference <= 0) {
            throw new IllegalArgumentException("두 거리의 차이가 너무 작습니다.");
        }
        return new Distance(difference);
    }

    /**
     * 두 거리를 더합니다.
     * @param distance
     * @return
     */
    public Distance add(Distance distance) {
        return new Distance(this.getDistance() + distance.getDistance());
    }

    public int getDistance() {
        return distance;
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
        return "distance=" + distance;
    }
}
