package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DuplicateLineException.class)
    public ResponseEntity<String> handleDuplicateLineException(DuplicateLineException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity<String> handleLineNotFoundException(LineNotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
