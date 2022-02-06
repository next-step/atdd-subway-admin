package nextstep.subway.line.exception;

import nextstep.subway.common.exception.InvalidValueException;

public class SectionAlreadyExistInTheLineException extends InvalidValueException {

    public SectionAlreadyExistInTheLineException(String message) {
        super(message);
    }
}
