package nextstep.subway.station.domain;

import nextstep.subway.station.exception.InvalidDistanceException;

import javax.persistence.*;
import java.util.Objects;
import static java.util.Objects.requireNonNull;

@Embeddable
public class Distance implements Comparable<Distance> {

    private Long distance;

    public Distance(Long distance) {
        this.distance = requireNonNull(distance, "구간 길이가 비었습니다");
    }

    protected Distance() {
    }

    public void minus(Distance distance) {
        long result = this.distance - distance.distance;
        if (result <= 0) {
            throw new InvalidDistanceException();
        }
        this.distance = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance other = (Distance) o;
        return compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

    @Override
    public int compareTo(Distance other) {
        return Long.compare(distance, other.distance);
    }
}
