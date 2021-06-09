package nextstep.subway.exception;

public class NotExistLineException extends RuntimeException {

    private final String message = "요청한 Line 이 없습니다";

    @Override
    public String getMessage() {
        return message;
    }
}
