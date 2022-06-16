package nextstep.subway.ui;

import java.util.NoSuchElementException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SubwayControllerAdvice {
    @ExceptionHandler({DataIntegrityViolationException.class,
            NoSuchElementException.class,
            IllegalArgumentException.class})
    public ResponseEntity handleExceptions() {
        return ResponseEntity.badRequest().build();
    }
}
