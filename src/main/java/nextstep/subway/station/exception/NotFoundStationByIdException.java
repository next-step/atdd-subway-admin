package nextstep.subway.station.exception;

public class NotFoundStationByIdException extends IllegalArgumentException {

    private static final String DEFAULT_MESSAGE = "해당 아이디로 지하철 역을 찾을 수 없습니다.";

    public NotFoundStationByIdException() {
        super(DEFAULT_MESSAGE);
    }

    public NotFoundStationByIdException(String message) {
        super(message);
    }
}
