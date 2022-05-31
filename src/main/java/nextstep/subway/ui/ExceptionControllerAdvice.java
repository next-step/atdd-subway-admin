package nextstep.subway.ui;

import javax.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Void> entityNotFound(EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
