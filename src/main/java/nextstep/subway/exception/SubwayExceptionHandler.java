package nextstep.subway.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(annotations = RestController.class)
public class SubwayExceptionHandler {
    @ExceptionHandler(NotFoundStation.class)
    public ResponseEntity<String> notFoundStationException(NotFoundStation ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundLine.class)
    public ResponseEntity<String> notFoundLineException(NotFoundLine ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalRequestBody.class)
    public ResponseEntity<String> illegalRequestBodyException(IllegalRequestBody ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
