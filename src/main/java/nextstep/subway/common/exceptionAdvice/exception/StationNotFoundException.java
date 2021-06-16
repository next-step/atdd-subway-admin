package nextstep.subway.common.exceptionAdvice.exception;

import nextstep.subway.common.exceptionAdvice.ErrorCode;

public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException(Long id) {
        super(String.format(ErrorCode.STATION_NOT_FOUND_EXCEPTION.getErrorMessage(), id));
    }
}
