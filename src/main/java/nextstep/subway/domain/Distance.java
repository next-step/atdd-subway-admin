package nextstep.subway.domain;

import nextstep.subway.exception.BadRequestException;

public class Distance {
    private Long distance;

    protected Distance() {
    }

    public Distance(Long distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    public void updateDistance(Distance newDistance) {
        validateDistance(this.distance - newDistance.getDistance());
        this.distance = this.distance - newDistance.getDistance();
    }

    public void validateDistance(Long distance) {
        if (distance < 1) {
            System.out.println("Distance는 0보다 큰 값이 여야 합니다.");
            throw new BadRequestException("Distance는 0보다 큰 값이 여야 합니다.");
        }
    }

    public Long getDistance() {
        return distance;
    }
}
