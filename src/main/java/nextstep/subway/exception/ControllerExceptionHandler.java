package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity dataNotFoundExceptionHandler(DataNotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(StationNotExistException.class)
    public ResponseEntity stationNotExistExceptionHandler(StationNotExistException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(SectionDuplicateException.class)
    public ResponseEntity sectionDuplicateExceptionHandler(SectionDuplicateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(SectionDistanceException.class)
    public ResponseEntity sectionDistanceExceptionHandler(SectionDistanceException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
