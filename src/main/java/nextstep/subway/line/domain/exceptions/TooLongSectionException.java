package nextstep.subway.line.domain.exceptions;

public class TooLongSectionException extends RuntimeException {
    public TooLongSectionException(final String message) {
        super(message);
    }
}
