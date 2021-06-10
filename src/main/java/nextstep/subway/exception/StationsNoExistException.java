package nextstep.subway.exception;

public class StationsNoExistException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StationsNoExistException() {
        super();
    }

    public StationsNoExistException(String message) {
        super(message);
    }
}
