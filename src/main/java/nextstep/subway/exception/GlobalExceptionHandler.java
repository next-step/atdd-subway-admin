package nextstep.subway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ErrorResponse> handleIllegalArgument(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(ErrorMessage.INTERNAL_SERVER_ERROR.getMessage()));
    }

}
