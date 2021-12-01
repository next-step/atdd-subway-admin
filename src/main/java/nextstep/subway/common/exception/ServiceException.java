package nextstep.subway.common.exception;

public class ServiceException extends RuntimeException {

    private static final String MESSAGE_BAD_REQUEST = "요청중 에러가 발생했습니다.";

    public ServiceException() {
        super(MESSAGE_BAD_REQUEST);
    }

    public ServiceException(String message) {
        super(message);
    }
}
