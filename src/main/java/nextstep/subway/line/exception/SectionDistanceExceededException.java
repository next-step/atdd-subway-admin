package nextstep.subway.line.exception;

import nextstep.subway.common.exception.InvalidValueException;

public class SectionDistanceExceededException extends InvalidValueException {

    public SectionDistanceExceededException(String message) {
        super(message);
    }
}
