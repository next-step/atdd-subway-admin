package nextstep.subway.station.exception;

import static nextstep.subway.common.Message.*;

import javax.persistence.EntityNotFoundException;

public class StationNotFoundException extends EntityNotFoundException {

    public StationNotFoundException() {
        super(STATION_NOTFOUND_EXCEPTION.getMessage());
    }

    public StationNotFoundException(String message) {
        super(message);
    }
}
