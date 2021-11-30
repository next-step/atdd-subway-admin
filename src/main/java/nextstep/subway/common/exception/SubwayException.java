package nextstep.subway.common.exception;

public class SubwayException extends RuntimeException {
    public SubwayException(SubwayErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
