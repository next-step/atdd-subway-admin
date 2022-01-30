package nextstep.subway.line.exception;

import nextstep.subway.common.exception.InvalidValueException;

public class NameDuplicateException extends InvalidValueException {

    public NameDuplicateException(String message) {
        super(message);
    }
}
