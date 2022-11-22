package nextstep.subway.ui;

import nextstep.subway.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class SubwayExceptionHandler {
    @ExceptionHandler(NotFoundStationException.class)
    public ResponseEntity<String> notFoundStationException(NotFoundStationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundLineException.class)
    public ResponseEntity<String> notFoundLineException(NotFoundLineException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(NotFoundSectionException.class)
    public ResponseEntity<String> notFoundSectionException(NotFoundSectionException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(DeleteSectionFailedException.class)
    public ResponseEntity<String> deleteSectionException(DeleteSectionFailedException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalRequestBodyException.class)
    public ResponseEntity<String> illegalRequestBodyException(IllegalRequestBodyException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
