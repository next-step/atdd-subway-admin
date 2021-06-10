package nextstep.subway.station.application.exception;

public class StationNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StationNotFoundException(String message) {
        super(message);
    }
}
