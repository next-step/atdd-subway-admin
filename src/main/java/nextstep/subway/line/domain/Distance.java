package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import nextstep.subway.common.exception.ServiceException;

@Embeddable
public class Distance {

    public static final String SECTION_DISTANCE_NOT_LESS_THAN_ZERO = "구간의 거리가 0보다 작을 수 없습니다.";

    @Column
    private int distance = 0;

    public Distance(int distance) {
        if (distance < 0) {
            throw new ServiceException(SECTION_DISTANCE_NOT_LESS_THAN_ZERO);
        }
        this.distance = distance;
    }

    protected Distance() {
    }

    public int getDistance() {
        return distance;
    }
}
