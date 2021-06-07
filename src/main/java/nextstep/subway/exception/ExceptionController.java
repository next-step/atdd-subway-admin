package nextstep.subway.exception;

import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DuplicateDataExistsException.class)
    public ResponseEntity<LineResponse> handleDuplicateDataExistsException(DuplicateDataExistsException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<LineResponse> handleDataNotFoundException(DataNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
