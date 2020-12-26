package nextstep.subway.line.ui;

import nextstep.subway.line.application.exceptions.LineNotFoundException;
import nextstep.subway.line.domain.exceptions.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class LineControllerAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity handleLineNotFoundException(LineNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidSectionException.class)
    public ResponseEntity handleInvalidSectionException(InvalidSectionException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity handleStationNotFoundException(StationNotFoundException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(TooLongSectionException.class)
    public ResponseEntity handleTooLongSectionException(TooLongSectionException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(TargetSectionNotFoundException.class)
    public ResponseEntity handleTargetSectionNotFoundException(TargetSectionNotFoundException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity handleNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
