package nextstep.subway.exception;

public class DefaultException extends RuntimeException{

    public static final String UNEXPECTED_ERROR = "예기치 못한 오류가 발생하였습니다.";

    public DefaultException() {
        super(UNEXPECTED_ERROR);
    }
}
