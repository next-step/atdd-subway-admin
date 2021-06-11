package nextstep.subway.enums;


import nextstep.subway.common.ErrorMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public enum ErrorCode {

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "Invalid Input Value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Invalid Method"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access is Denied"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal Server Error");


    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public ResponseEntity<ErrorMessageResponse> createResponseEntity() {
        return ResponseEntity.status(httpStatus).body(ErrorMessageResponse.of(message));
    }

    public ResponseEntity<ErrorMessageResponse> createResponseEntity(Throwable throwable) {
        return ResponseEntity.status(httpStatus).body(ErrorMessageResponse.of(throwable.getMessage()));
    }
}
