package nextstep.subway.station.exception;

import nextstep.subway.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class StationNotFoundException extends ServiceException {

    public StationNotFoundException(final String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
