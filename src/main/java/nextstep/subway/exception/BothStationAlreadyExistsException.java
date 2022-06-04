package nextstep.subway.exception;

import static nextstep.subway.constant.ExceptionMessages.BOTH_STATION_ALREADY_EXISTS;

public class BothStationAlreadyExistsException extends RuntimeException {
    public BothStationAlreadyExistsException() {
        super(BOTH_STATION_ALREADY_EXISTS);
    }
}
