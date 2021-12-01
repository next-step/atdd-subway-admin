package nextstep.subway.line.exception;

import nextstep.subway.common.exception.ServiceException;

public class AlreadyRegisteredException extends ServiceException {
    public static final String MESSAGE_ALREADY_REGISTERED_SECTION = "이미 등록되어 있는 구간입니다.";

    public AlreadyRegisteredException() {
        super(MESSAGE_ALREADY_REGISTERED_SECTION);
    }

    public AlreadyRegisteredException(String message) {
        super(message);
    }
}
