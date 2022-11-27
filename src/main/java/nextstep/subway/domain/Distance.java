package nextstep.subway.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    private static final int TERMINAL_SECTION_DISTANCE = 0;
    private static final int MINIMUM_DISTANCE = 0;

    private int distance;


    protected Distance() {

    }

    public Distance(int distance) {
        if (distance <= MINIMUM_DISTANCE) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_DISTANCE_VALUE.getMessage());
        }
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public Distance compareTo(Distance distance) {
        int newDistance = this.distance - distance.getDistance();
        if (newDistance <= MINIMUM_DISTANCE) {
            throw new IllegalArgumentException(ErrorMessage.EXCEED_SECTION_DISTANCE.getMessage());
        }
        return new Distance(newDistance);
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

    public void setEndSectionDistance() {
        this.distance = TERMINAL_SECTION_DISTANCE;
    }

    public Distance addDistance(Distance distance) {
        this.distance += distance.getDistance();
        return this;
    }

    public static Distance getTerminalSectionDistance() {
        Distance distance = new Distance();
        distance.setEndSectionDistance();
        return distance;
    }
}
