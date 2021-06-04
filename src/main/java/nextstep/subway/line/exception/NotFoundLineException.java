package nextstep.subway.line.exception;

import javax.persistence.EntityNotFoundException;

public class NotFoundLineException extends EntityNotFoundException {

    public static final String NOT_FOUND_LINE_MESSAGE = "해당 id의 노선이 존재하지 않습니다.";

    public NotFoundLineException() {
        super(NOT_FOUND_LINE_MESSAGE);
    }

    public NotFoundLineException(String message) {
        super(message);
    }
}
