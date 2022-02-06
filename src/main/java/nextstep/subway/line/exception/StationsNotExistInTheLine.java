package nextstep.subway.line.exception;

import nextstep.subway.common.exception.InvalidValueException;

public class StationsNotExistInTheLine extends InvalidValueException {

    public StationsNotExistInTheLine(String message) {
        super(message);
    }
}
