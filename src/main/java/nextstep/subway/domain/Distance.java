package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final String ERROR_MESSAGE_NOT_NULL_DISTANCE = "거리는 필수입니다.";
    private static final String ERROR_MESSAGE_GRATER_THAN_ZERO_DISTANCE = "거리는 0보다 커야합니다.";
    public static final String ERROR_MESSAGE_VALID_DISTANCE = "기존 노선의 거리보다 작거나 같을 수 없습니다.";

    @Column(nullable = false)
    private Integer distance;

    protected Distance() {
    }

    protected Distance(Integer distance) {
        validDistance(distance);
        this.distance = distance;
    }

    private void validDistance(Integer distance) {
        if (Objects.isNull(distance)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_NULL_DISTANCE);
        }

        if (distance <= 0) {
            throw new IllegalArgumentException(ERROR_MESSAGE_GRATER_THAN_ZERO_DISTANCE);
        }
    }

    public Distance subtract(Distance distance) {
        try {
            return Distance.from(this.distance - distance.distance);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(ERROR_MESSAGE_VALID_DISTANCE);
        }
    }

    public Distance add(Distance distance) {
        return Distance.from(this.distance + distance.distance);
    }

    public static Distance from(Integer distance) {
        return new Distance(distance);
    }

    public Integer getDistance() {
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
        return Objects.hash(distance);
    }
}