package nextstep.subway.line.application;

public class LineDuplicatedException extends RuntimeException {

    public LineDuplicatedException() {
        super();
    }

    public LineDuplicatedException(String message) {
        super(message);
    }

    public LineDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LineDuplicatedException(Throwable cause) {
        super(cause);
    }
}
