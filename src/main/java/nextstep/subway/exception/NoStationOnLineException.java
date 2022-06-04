package nextstep.subway.exception;

public class NoStationOnLineException extends RuntimeException {
    public NoStationOnLineException(String message) {
        super(message);
    }
}
