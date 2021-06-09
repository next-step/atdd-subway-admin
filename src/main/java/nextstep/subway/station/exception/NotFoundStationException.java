package nextstep.subway.station.exception;

import javax.persistence.EntityNotFoundException;

public class NotFoundStationException extends EntityNotFoundException {
    public NotFoundStationException() {
        super();
    }

    public NotFoundStationException(String message) {
        super(message);
    }
}
