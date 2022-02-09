package nextstep.subway.line.exception;

import nextstep.subway.common.exception.InvalidValueException;

public class SectionEmptyException extends InvalidValueException {

    public SectionEmptyException(String message) {
        super(message);
    }
}
