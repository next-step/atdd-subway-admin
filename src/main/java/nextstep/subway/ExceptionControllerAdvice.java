package nextstep.subway;

import nextstep.subway.exception.CannotDeleteException;
import nextstep.subway.exception.DataNotFoundException;
import nextstep.subway.exception.DuplicateDataExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(DuplicateDataExistsException.class)
    public ResponseEntity<Void> handleDuplicateDataExistsException(DuplicateDataExistsException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Void> handleDataNotFoundException(DataNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(CannotDeleteException.class)
    public ResponseEntity<Void> cannotDeleteException(CannotDeleteException e) {
        return ResponseEntity.badRequest().build();
    }
}
