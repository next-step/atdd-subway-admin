package nextstep.subway.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateValueException extends DataIntegrityViolationException {

    public DuplicateValueException(String msg) {
        super(msg);
    }

    public DuplicateValueException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
