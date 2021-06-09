package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(DuplicateDataExistsException.class)
    public <T> ResponseEntity<T> handleDuplicateDataExistsException(DuplicateDataExistsException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DataNotFoundException.class)
    public <T> ResponseEntity<T> handleDataNotFoundException(DataNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
