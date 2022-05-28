package nextstep.subway.exception;

public class LineNotFoundException extends RuntimeException {

    public LineNotFoundException() {
        super();
    }

    public LineNotFoundException(String message) {
        super(message);
    }
}
