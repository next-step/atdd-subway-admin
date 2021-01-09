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
     * 앞의 거리 값을 기준으로 두 거리의 차이가 없거나 0보다 작으면 오류를 발생합니다.
     * @param distance1
     * @param distance2
     * @return 두거리의 차이
     * @throws IllegalArgumentException
     */
    public static Distance difference(Distance distance1, Distance distance2) {
        int difference = distance1.getDistance() - distance2.distance;
        if(difference <= 0) {
            throw new IllegalArgumentException("두 거리의 차이가 너무 작습니다.");
        }
        return new Distance(difference);
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
