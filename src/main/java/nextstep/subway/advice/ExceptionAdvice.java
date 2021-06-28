package nextstep.subway.advice;

import nextstep.subway.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotExistLineException.class)
    public ResponseEntity handleNotExistException(NotExistLineException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DuplicateSectionException.class)
    public ResponseEntity handleDuplicateSectionException(DuplicateSectionException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidateDistanceException.class)
    public ResponseEntity handleInvalidateDistanceException(InvalidateDistanceException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotContainSectionException.class)
    public ResponseEntity handleNotContainSectionException(NotContainSectionException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(CannotRemoveSingleSectionException.class)
    public ResponseEntity handleCannotRemoveSingleSectionException(CannotRemoveSingleSectionException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
