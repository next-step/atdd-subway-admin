package nextstep.subway.line.domain.exceptions;

public class InvalidStationDeleteTryException extends RuntimeException {
    public InvalidStationDeleteTryException(final String message) {
        super(message);
    }
}
