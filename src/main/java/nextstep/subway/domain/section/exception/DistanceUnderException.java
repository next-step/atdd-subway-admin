package nextstep.subway.domain.section.exception;

import nextstep.subway.global.error.ErrorCode;
import nextstep.subway.global.error.exception.BusinessException;

public class DistanceUnderException extends BusinessException {

    public DistanceUnderException() {
        super(ErrorCode.DISTANCE_UNDER);
    }
}
