package nextstep.subway.common.exception;

import java.util.NoSuchElementException;

public class StationNotFoundException extends NoSuchElementException {

    private static String STATION_NOT_FOUND_ERROR = "존재하지 않는 지하철역 입니다.";

    public StationNotFoundException() {
        super(STATION_NOT_FOUND_ERROR);
    }

    public StationNotFoundException(final String message) {
        super(message);
    }
}
