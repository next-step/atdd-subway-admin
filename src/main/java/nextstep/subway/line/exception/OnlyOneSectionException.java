package nextstep.subway.line.exception;

import static nextstep.subway.common.Message.*;

import nextstep.subway.common.exception.ServiceException;

public class OnlyOneSectionException extends ServiceException {

    public OnlyOneSectionException() {
        super(MESSAGE_ONLY_ONE_SECTION.getMessage());
    }

    public OnlyOneSectionException(String message) {
        super(message);
    }
}
