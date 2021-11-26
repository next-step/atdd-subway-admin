package nextstep.subway.common.exception;

public class IllegalStationException extends IllegalArgumentException {

    private static final String ILLEGAL_STATION_ERROR = "올바르지 않은 지하철 역입니다.";

    public IllegalStationException() {
        super(ILLEGAL_STATION_ERROR);
    }

    public IllegalStationException(final String message) {
        super(message);
    }
}
