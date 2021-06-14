package nextstep.subway.linestation.exception;

public class DuplicateLineStationException extends RuntimeException {

    public DuplicateLineStationException() {
    }

    public DuplicateLineStationException(final String message) {
        super(message);
    }
}
