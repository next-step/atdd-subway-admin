package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SubwayCustomException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public SubwayCustomException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public SubwayCustomException(ExceptionType exceptionType) {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = exceptionType.getMessage();
    }

    public SubwayCustomException(HttpStatus status, ExceptionType exceptionType) {
        this.status = status;
        this.message = exceptionType.getMessage();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
