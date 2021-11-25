package nextstep.subway.domain.section.exception;

import nextstep.subway.global.error.ErrorCode;
import nextstep.subway.global.error.exception.BusinessException;

public class DistanceExcessException extends BusinessException {

    public DistanceExcessException() {
        super(ErrorCode.DISTANCE_EXCESS);
    }
}
