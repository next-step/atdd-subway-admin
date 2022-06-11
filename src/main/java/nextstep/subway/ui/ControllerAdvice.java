package nextstep.subway.ui;

import nextstep.subway.exception.InvalidDistanceException;
import nextstep.subway.exception.InvalidSectionException;
import nextstep.subway.exception.SectionDeleteException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleDataIntegrityViolationException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidDistanceException.class)
    public ResponseEntity<Void> handleInvalidDistanceException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidSectionException.class)
    public ResponseEntity<Void> handleInvalidSectionException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(SectionDeleteException.class)
    public ResponseEntity<Void> handleSectionDeleteException() {
        return ResponseEntity.badRequest().build();
    }

}
