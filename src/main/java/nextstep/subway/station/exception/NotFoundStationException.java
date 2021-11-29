package nextstep.subway.station.exception;

public class NotFoundStationException extends RuntimeException {

    public static final String NOT_FOUND_STATION = "지하철역이 존재하지 않습니다.";

    public NotFoundStationException() {
        super(NOT_FOUND_STATION);
    }
}
