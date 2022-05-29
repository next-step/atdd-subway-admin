package nextstep.subway.exception;

public class StationNotFoundException extends RuntimeException {

    public StationNotFoundException() {
        super();
    }

    public StationNotFoundException(String message) {
        super(message);
    }
}
