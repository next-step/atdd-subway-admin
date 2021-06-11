package nextstep.subway.section.exception;

public class ExistSameStationsException extends RuntimeException {
    public ExistSameStationsException() {
    }

    public ExistSameStationsException(String message) {
        super(message);
    }
}
