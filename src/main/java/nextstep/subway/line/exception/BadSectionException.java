package nextstep.subway.line.exception;

import nextstep.subway.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class BadSectionException extends ServiceException {

    public BadSectionException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
