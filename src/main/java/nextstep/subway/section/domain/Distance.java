package nextstep.subway.section.domain;

import nextstep.subway.excetpion.ErrorCode;
import nextstep.subway.station.exception.StationNotFoundException;

import javax.persistence.Embeddable;

import static nextstep.subway.common.utils.ValidationUtils.isNull;

@Embeddable
public class Distance {

    private static final int DISTANCE_ZERO = 0;

    private int distance;

    protected Distance() {}

    public Distance(int distance) {
        validation(distance);
        this.distance = distance;
    }

    public void validation(int distance) {
        if (isNull(distance) || distance == DISTANCE_ZERO) {
            throw new StationNotFoundException(ErrorCode.NOT_FOUND_ARGUMENT, "구간의 길이가 없습니다.");
        }
    }
}
