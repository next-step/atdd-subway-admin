package nextstep.subway.exception;

import static nextstep.subway.message.ErrorMessage.DATA_INTEGRITY_VIOLATION_ERROR_MESSAGE;

import nextstep.subway.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgument(RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.createErrorResponse(e.getMessage()));
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgsException() {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.createErrorResponse(DATA_INTEGRITY_VIOLATION_ERROR_MESSAGE.toMessage()));
    }
}
