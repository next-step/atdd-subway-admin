package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final String NOT_ENOUGH_DISTANCE = "충분하지 않은 구간 크기입니다.";
    private static final String NOT_ADD_ENOUGH_DISTANCE = "추가하기에 충분하지 않은 구간 크기입니다.";
    private final int MINIMUM_DISTANCE = 0;

    private int distance;

    protected Distance() {
    }

    private Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    public static Distance of(int distance){
        return new Distance(distance);
    }

    private void validate(int distance) {
        if (distance <= MINIMUM_DISTANCE) {
            throw new IllegalArgumentException(NOT_ENOUGH_DISTANCE);
        }
    }

    public void subtractDiffDistance(int distance) {
        int diff = this.distance - distance;
        if (diff <= MINIMUM_DISTANCE) {
            throw new IllegalArgumentException(NOT_ADD_ENOUGH_DISTANCE);
        }
        this.distance = diff;
    }

    public void addDistance(int distance) {
        this.distance += distance;
    }

    public int toNumber() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Distance distance1 = (Distance)o;
        return MINIMUM_DISTANCE == distance1.MINIMUM_DISTANCE && distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(MINIMUM_DISTANCE, distance);
    }
}
