package nextstep.subway.exception;

import nextstep.subway.line.dto.LineResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundEntityException.class)
    public ResponseEntity<LineResponse> handleNotFoundException(NotFoundEntityException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<LineResponse> handleIllegalDataException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidSectionException.class)
    public ResponseEntity<LineResponse> handleInvalidSectionException(InvalidSectionException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<LineResponse> handleIllegalDataException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
}
