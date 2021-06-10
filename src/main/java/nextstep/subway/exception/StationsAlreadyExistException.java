package nextstep.subway.exception;

public class StationsAlreadyExistException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StationsAlreadyExistException() {
        super();
    }

    public StationsAlreadyExistException(String message) {
        super(message);
    }
}
