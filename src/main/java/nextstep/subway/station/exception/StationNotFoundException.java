package nextstep.subway.station.exception;

import javax.persistence.EntityNotFoundException;

public class StationNotFoundException extends EntityNotFoundException {

    public static final String STATION_NOTFOUND_EXCEPTION = "지하철역이 존재하지 않습니다.";

    public StationNotFoundException(String message) {
        super(message);
    }

    public StationNotFoundException() {
        super(STATION_NOTFOUND_EXCEPTION);
    }
}
