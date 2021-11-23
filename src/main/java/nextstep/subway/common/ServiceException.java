package nextstep.subway.common;

import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {

    private static final String SERVICE_MESSAGE = "요청중 에러가 발생했습니다.";

    ErrorResponse errorResponse;

    public ServiceException() {
        ErrorResponse.of(HttpStatus.BAD_REQUEST, SERVICE_MESSAGE);
    }

    public ServiceException(String message) {
        super(message);
    }
}
