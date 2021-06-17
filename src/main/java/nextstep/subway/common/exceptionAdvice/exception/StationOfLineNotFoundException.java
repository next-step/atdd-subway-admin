package nextstep.subway.common.exceptionAdvice.exception;

import nextstep.subway.common.exceptionAdvice.ErrorCode;

public class StationOfLineNotFoundException extends RuntimeException {
    public StationOfLineNotFoundException(Long id) {
        super(String.format(ErrorCode.STATION_OF_LINE_NOT_FOUND_EXCEPTION.getErrorMessage(), id));
    }
}
