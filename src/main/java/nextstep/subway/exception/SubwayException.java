package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SubwayException extends RuntimeException {

    private HttpStatus httpStatus;

    public SubwayException(SubWayExceptionStatus status) {
        super(status.getMessage());
        this.httpStatus  = status.getHttpStatus();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
