package nextstep.subway.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayExceptionHandler {
    @ExceptionHandler({NotFoundStation.class})
    public ResponseEntity<String> notFoundStationException(NotFoundStation ex) {
        return ResponseEntity.badRequest().body("not found station");
    }
    @ExceptionHandler({NotFoundLine.class})
    public ResponseEntity<String> notFoundLineException(NotFoundStation ex) {
        return ResponseEntity.badRequest().body("not found line");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
