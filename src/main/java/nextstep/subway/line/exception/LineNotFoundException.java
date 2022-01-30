package nextstep.subway.line.exception;

import nextstep.subway.common.exception.EntityNotFoundException;

public class LineNotFoundException extends EntityNotFoundException {

    public LineNotFoundException(String message) {
        super(message);
    }
}
