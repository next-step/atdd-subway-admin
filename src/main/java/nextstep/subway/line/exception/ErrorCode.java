package nextstep.subway.line.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCode {

    DEFAULT_ERROR(INTERNAL_SERVER_ERROR, "internal_error"),
    NOT_FOUND_ENTITY(BAD_REQUEST, "not found entity"),
    ALREADY_EXIST_ENTITY(BAD_REQUEST, "already exist entity");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public static ErrorCode of(HttpStatus httpStatus) {
        for (ErrorCode value : ErrorCode.values()) {
            if (value.compareTo(httpStatus)) {
                return value;
            }
        }
        return DEFAULT_ERROR;
    }

    private boolean compareTo(HttpStatus httpStatus) {
        return this.httpStatus.equals(httpStatus);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
