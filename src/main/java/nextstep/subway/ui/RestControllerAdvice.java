package nextstep.subway.ui;

import javassist.NotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity handleStationNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleDataIntegrityViolationException() {
        return ResponseEntity.badRequest().build();
    }
}
