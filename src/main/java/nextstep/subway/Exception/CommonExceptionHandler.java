package nextstep.subway.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundStationException.class)
    public ResponseEntity handleNotFoundStationException(NotFoundStationException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(NotFoundLineException.class)
    public ResponseEntity handleNotFoundLineException(NotFoundLineException ex) {
        return ResponseEntity.notFound().build();
    }
}
