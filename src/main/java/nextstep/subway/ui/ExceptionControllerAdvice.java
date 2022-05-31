package nextstep.subway.ui;

import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.exception.CannotAddSectionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Void> entityNotFound(EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    }


    @ExceptionHandler(value = {CannotAddSectionException.class})
    public ResponseEntity<String> cannotAddSection(CannotAddSectionException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}
