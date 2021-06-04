package nextstep.subway.line.exception;

import javax.persistence.EntityNotFoundException;

public class NotFoundLineException extends EntityNotFoundException {

    public NotFoundLineException() {
        super();
    }

    public NotFoundLineException(String message) {
        super(message);
    }
}
