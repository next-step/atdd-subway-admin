package nextstep.subway.common.exception;

import nextstep.subway.line.exception.NotFoundLineException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonControllerAdvice {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({NotFoundLineException.class})
    public ResponseEntity handleNotFoundLineException(NotFoundLineException e) {
        return ResponseEntity.noContent().build();
    }

}
