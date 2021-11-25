package nextstep.subway.domain.section.exception;

import nextstep.subway.global.error.ErrorCode;
import nextstep.subway.global.error.exception.BusinessException;

public class StandardStationNotFoundException extends BusinessException {

    public StandardStationNotFoundException() {
        super(ErrorCode.STANDARD_STATION_NOT_FOUND);
    }
}
