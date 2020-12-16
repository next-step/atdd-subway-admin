package nextstep.subway.line.application.exceptions;

public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException(String message) {
        super(message);
    }
}
