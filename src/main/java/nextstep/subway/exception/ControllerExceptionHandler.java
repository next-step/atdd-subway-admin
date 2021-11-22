package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity dataNotFoundExceptionHandler(DataNotFoundException e) {
        return ResponseEntity.badRequest().build();
    }
}
