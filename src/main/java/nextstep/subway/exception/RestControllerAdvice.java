package nextstep.subway.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity<String> handleLineNotFoundException(LineNotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity<String> handleStationNotFoundException(StationNotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalStationDistanceException.class)
    public ResponseEntity<String> handleIllegalStationDistanceException(IllegalStationDistanceException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(SectionExistException.class)
    public ResponseEntity<String> handleSectionExistException(SectionExistException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(StationNotContainInUpOrDownStation.class)
    public ResponseEntity<String> handleStationNotContainInUpOrDownStation(StationNotContainInUpOrDownStation e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
