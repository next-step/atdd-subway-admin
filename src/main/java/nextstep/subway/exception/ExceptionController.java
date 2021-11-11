package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DuplicateLineException.class)
    public ResponseEntity duplicateUserException(DuplicateLineException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
