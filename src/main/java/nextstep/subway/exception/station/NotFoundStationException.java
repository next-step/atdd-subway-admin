package nextstep.subway.exception.station;

public class NotFoundStationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String NO_STATION = "존재하지 않는 역입니다.";

    public NotFoundStationException() {
        super(NO_STATION);
    }

    public NotFoundStationException(String message) {
        super(message);
    }
}
