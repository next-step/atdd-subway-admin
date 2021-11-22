package nextstep.subway.station.exception;

import javax.persistence.EntityNotFoundException;

public class StationNotFoundException extends EntityNotFoundException {
    public StationNotFoundException() {
        super();
    }

    public StationNotFoundException(String message) {
        super(message);
    }
}
