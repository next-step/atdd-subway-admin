package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DuplicateEntityExistsException.class)
    public ResponseEntity handleDuplicateEntityExistsException(DuplicateEntityExistsException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity handleDataNotFoundException(DataNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
