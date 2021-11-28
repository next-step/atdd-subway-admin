package nextstep.subway.common.exception;

import nextstep.subway.line.exception.AlreadyRegisteredSectionException;
import nextstep.subway.line.exception.LongDistanceException;
import nextstep.subway.line.exception.NotFoundLineException;
import nextstep.subway.line.exception.NotFoundUpAndDownStation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler({AlreadyRegisteredSectionException.class, NotFoundUpAndDownStation.class, LongDistanceException.class})
    public ResponseEntity handleAlreadyRegisteredSectionException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
    }

}
