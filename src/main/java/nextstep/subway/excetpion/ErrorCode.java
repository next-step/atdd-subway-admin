package nextstep.subway.excetpion;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCode {

    DEFAULT_ERROR(INTERNAL_SERVER_ERROR, "internal_error"),
    BAD_ARGUMENT(BAD_REQUEST, "bad argument"),
    NOT_FOUND_ARGUMENT(BAD_REQUEST, "not found argument"),
    NOT_FOUND_ENTITY(BAD_REQUEST, "not found entity"),
    ALREADY_EXIST_ENTITY(BAD_REQUEST, "already exist entity");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
