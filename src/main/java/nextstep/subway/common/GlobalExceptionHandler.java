package nextstep.subway.common;

import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
        log.error("handleIllegalArgsException", e);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("handleEntityNotFoundException", e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity handleException(Exception e) {
        log.error("handleException", e);
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
