package nextstep.subway.line.domain.exceptions;

public class TargetSectionNotFoundException extends RuntimeException {
    public TargetSectionNotFoundException(final String message) {
        super(message);
    }
}
