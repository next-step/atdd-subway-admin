package nextstep.subway.line.domain.exceptions;

public class NotExistSectionAddPolicy extends RuntimeException {
    public NotExistSectionAddPolicy(final String message) {
        super(message);
    }
}
