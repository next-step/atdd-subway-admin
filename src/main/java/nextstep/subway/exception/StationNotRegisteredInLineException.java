package nextstep.subway.exception;

import static nextstep.subway.constant.ExceptionMessages.STATION_NOT_REGISTERED_IN_LINE;

public class StationNotRegisteredInLineException extends RuntimeException {
    public StationNotRegisteredInLineException() {
        super(STATION_NOT_REGISTERED_IN_LINE);
    }
}
