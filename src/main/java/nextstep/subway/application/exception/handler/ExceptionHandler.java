package nextstep.subway.application.exception.handler;

import nextstep.subway.application.exception.exception.AlreadyDataException;
import nextstep.subway.application.exception.exception.NotFoundDataException;
import nextstep.subway.application.exception.exception.NotValidDataException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(
            {DataIntegrityViolationException.class, NotFoundDataException.class, NotValidDataException.class, AlreadyDataException.class})
    protected ResponseEntity<Void> handleException() {
        return ResponseEntity.badRequest().build();
    }
}
