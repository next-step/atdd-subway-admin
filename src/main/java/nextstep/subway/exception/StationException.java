package nextstep.subway.exception;

public class StationException extends RuntimeException {

    public StationException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
    }
}
