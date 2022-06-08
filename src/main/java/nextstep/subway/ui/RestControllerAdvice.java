package nextstep.subway.ui;

import javassist.NotFoundException;
import nextstep.subway.exception.DownStationNotFoundException;
import nextstep.subway.exception.UpStationNotFoundException;
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

    @ExceptionHandler(UpStationNotFoundException.class)
    public ResponseEntity handleUpStationNotFoundException() {
        return ResponseEntity.badRequest().build();
    }
    
    @ExceptionHandler(DownStationNotFoundException.class)
    public ResponseEntity handleDownStationNotFoundException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleDataIntegrityViolationException() {
        return ResponseEntity.badRequest().build();
    }
}
