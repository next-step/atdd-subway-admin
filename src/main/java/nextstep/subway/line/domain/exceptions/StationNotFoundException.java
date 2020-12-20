package nextstep.subway.line.domain.exceptions;

public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException(final String message) {
        super(message);
    }
}
