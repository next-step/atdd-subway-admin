package nextstep.subway.line.exception;

import nextstep.subway.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class DistanceOutOfRangeException extends ServiceException {

    public DistanceOutOfRangeException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
