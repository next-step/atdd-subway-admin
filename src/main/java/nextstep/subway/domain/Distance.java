package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;

@Embeddable
public class Distance {

    @Column(name = "distance")
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public void validDistance(Distance distance) {
        if (this.distance <= distance.getDistance()) {
            throw new SubwayException(ErrorCode.VALID_DISTANCE_ERROR);
        }
    }

    public void divideDistance(Distance distance) {
        this.distance -= distance.getDistance();
    }

    public void plusDistance(Distance distance) {
        this.distance += distance.getDistance();
    }
}
