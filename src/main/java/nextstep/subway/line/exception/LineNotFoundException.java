package nextstep.subway.line.exception;

import nextstep.subway.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class LineNotFoundException extends ServiceException {

    public LineNotFoundException(final String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
