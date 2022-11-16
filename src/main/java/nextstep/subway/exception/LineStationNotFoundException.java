package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LineStationNotFoundException extends RuntimeException {
    public LineStationNotFoundException(String message) {
        super(message);
    }
}
