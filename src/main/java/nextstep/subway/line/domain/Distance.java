package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    public static final String INVALID_DISTANCE_EXCEPTION_MESSAGE = "구간 거리는 0보다 커야합니다.";
    public static final String BIGGER_THAN_DISTANCE_EXCEPTION_MESSAGE = "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.";

    @Column
    private int distance;

    protected Distance() {
    }

    private Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    public static Distance valueOf(int distance){
        return new Distance(distance);
    }

    private void validate(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException(INVALID_DISTANCE_EXCEPTION_MESSAGE);
        }
    }

    public int getDistance() {
        return distance;
    }

    public Distance minus(Distance distanceToMinus) {
        if (!isAvailableMinus(distanceToMinus)) {
            throw new IllegalArgumentException(BIGGER_THAN_DISTANCE_EXCEPTION_MESSAGE);
        }
        return Distance.valueOf(distance - distanceToMinus.getDistance());
    }

    private boolean isAvailableMinus(Distance distanceToMinus){
        return distance > distanceToMinus.getDistance();
    }

    public Distance plus(Distance distanceToPlus) {
        return Distance.valueOf(distance + distanceToPlus.getDistance());
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
