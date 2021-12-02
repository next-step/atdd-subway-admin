package nextstep.subway.line.exception;

import static nextstep.subway.common.Message.*;

import nextstep.subway.common.exception.ServiceException;

public class LimitSectionSizeException extends ServiceException {

    public LimitSectionSizeException() {
        super(MESSAGE_LIMIT_SECTION_SIZE.getMessage());
    }

    public LimitSectionSizeException(String message) {
        super(message);
    }
}
