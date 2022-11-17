package nextstep.subway.common.handler;

import javassist.NotFoundException;
import nextstep.subway.common.exception.NotFoundDataException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(
            {DataIntegrityViolationException.class, NotFoundDataException.class})
    protected ResponseEntity<Void> handleException() {
        return ResponseEntity.badRequest().build();
    }
}
