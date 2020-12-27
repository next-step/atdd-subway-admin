package nextstep.subway.line.domain.exceptions;

public class EndUpStationNotFoundException extends RuntimeException {
    public EndUpStationNotFoundException(final String message) {
        super(message);
    }
}
