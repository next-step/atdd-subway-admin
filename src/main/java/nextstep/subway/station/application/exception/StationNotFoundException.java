package nextstep.subway.station.application.exception;

public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException() {
        super();
    }

    public StationNotFoundException(String message) {
        super(message);
    }

    public static StationNotFoundException error(String message) {
        return new StationNotFoundException(message);
    }
}
