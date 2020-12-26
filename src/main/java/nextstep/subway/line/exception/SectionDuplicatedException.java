package nextstep.subway.line.exception;

import nextstep.subway.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class SectionDuplicatedException extends ServiceException {

    public SectionDuplicatedException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
