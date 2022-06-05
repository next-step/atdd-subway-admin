package nextstep.subway.exception;

import static nextstep.subway.constant.ExceptionMessages.BOTH_STATION_NOT_EXISTS;

public class BothStationNotExistsException extends RuntimeException {
    public BothStationNotExistsException() {
        super(BOTH_STATION_NOT_EXISTS);
    }
}
