package nextstep.subway.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class BaseController {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleIllegalArgsException(final DataIntegrityViolationException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgsException(final IllegalArgumentException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest().build();
    }
}
