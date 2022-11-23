package nextstep.subway.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleDataIntegrityViolationException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<IllegalArgumentException> handleIllegalArgsException(Exception e) {
        return new ResponseEntity<>(new IllegalArgumentException(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataRemoveException.class)
    public ResponseEntity<DataRemoveException> handleDataRemoveException(Exception e) {
        return new ResponseEntity<>(new DataRemoveException(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
