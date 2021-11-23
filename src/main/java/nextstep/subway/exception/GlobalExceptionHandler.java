package nextstep.subway.exception;

import nextstep.subway.common.ErrorCode;
import nextstep.subway.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 404 에러
     */
    @ExceptionHandler(NotFoundApiException.class)
    public ResponseEntity<ErrorResponse> notFoundApiExceptionHandler(NotFoundApiException e) {
        ErrorCode errorCode = ErrorCode.valueOf(e.getMessage());
        return new ResponseEntity<>(ErrorResponse.of(errorCode), HttpStatus.NOT_FOUND);
    }
}
