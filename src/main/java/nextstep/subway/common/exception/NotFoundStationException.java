package nextstep.subway.common.exception;

public class NotFoundStationException extends RuntimeException {
    public static final String NOT_FOUND_STATION_MESSAGE = "역을 찾을 수 없습니다.";
    public NotFoundStationException() {
        super(NOT_FOUND_STATION_MESSAGE);
    }
}
