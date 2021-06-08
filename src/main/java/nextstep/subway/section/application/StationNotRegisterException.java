package nextstep.subway.section.application;

public class StationNotRegisterException extends RuntimeException {

    public StationNotRegisterException() {
        super();
    }

    public StationNotRegisterException(String message) {
        super(message);
    }

    public StationNotRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public StationNotRegisterException(Throwable cause) {
        super(cause);
    }
}
