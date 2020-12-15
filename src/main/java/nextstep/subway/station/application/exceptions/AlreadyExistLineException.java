package nextstep.subway.station.application.exceptions;

public class AlreadyExistLineException extends RuntimeException {
    public AlreadyExistLineException(String message) {
        super(message);
    }
}
