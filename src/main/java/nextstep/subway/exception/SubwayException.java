package nextstep.subway.exception;

public class SubwayException extends RuntimeException {

    public SubwayException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
    }
}
