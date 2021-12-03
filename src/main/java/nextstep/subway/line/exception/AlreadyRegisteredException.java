package nextstep.subway.line.exception;

import static nextstep.subway.common.Message.*;

import nextstep.subway.common.exception.ServiceException;

public class AlreadyRegisteredException extends ServiceException {


    public AlreadyRegisteredException() {
        super(MESSAGE_ALREADY_REGISTERED_SECTION.getMessage());
    }

    public AlreadyRegisteredException(String message) {
        super(message);
    }
}
