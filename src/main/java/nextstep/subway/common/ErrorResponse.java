package nextstep.subway.common;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private int status;
    private String message;

    private ErrorResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    public static ErrorResponse of (HttpStatus httpStatus, String message) {
        return new ErrorResponse(httpStatus, message);
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
