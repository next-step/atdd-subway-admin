package nextstep.subway.common.exceptionAdvice.exception;

import nextstep.subway.common.exceptionAdvice.ErrorCode;

public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException(Long id) {
        super(String.format(ErrorCode.LINE_NOT_FOUND_EXCEPTION.getErrorMessage(), id));
    }
}
