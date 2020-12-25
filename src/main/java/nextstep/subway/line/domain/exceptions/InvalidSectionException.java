package nextstep.subway.line.domain.exceptions;

public class InvalidSectionException extends RuntimeException {
    public InvalidSectionException(final String message) {
        super(message);
    }
}
