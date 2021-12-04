package nextstep.subway.line.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    
    private int distance;
    
    protected Distance() {
    }


    private Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }
    
    public static Distance from(int distance) {
        return new Distance(distance);
    }

    public int getDistance() {
        return distance;
    }
    
    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException(String.format("길이가 유효하지 않습니다.(%d)", distance));
        }
    }
    
    public Distance minus(Distance distance) {
        return Distance.from(this.distance - distance.getDistance());
    }
    
    public Distance plus(Distance distance) {
        return Distance.from(this.distance + distance.getDistance());
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
}
