package nextstep.subway.linestation.exception;

public class MethodNotAllowedException extends RuntimeException {

    public MethodNotAllowedException() {
    }

    public MethodNotAllowedException(final String message) {
        super(message);
    }
}
