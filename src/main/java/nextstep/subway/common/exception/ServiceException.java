package nextstep.subway.common.exception;

public class ServiceException extends RuntimeException {

    private static final String SERVICE_MESSAGE = "요청중 에러가 발생했습니다.";

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException() {
        super(SERVICE_MESSAGE);
    }

}
