package nextstep.subway.exception;

import javax.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.line.dto.LineResponse;

@RestControllerAdvice
public class LineControllerAdvice {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<LineResponse> noValidatedInputException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(StationsAlreadyExistException.class)
    public ResponseEntity<LineResponse> stationsAlreadyExistException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(StationsNoExistException.class)
    public ResponseEntity<LineResponse> stationsNoExistException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidDistanceException.class)
    public ResponseEntity<LineResponse> ㅑnvalidDistanceException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<LineResponse> notFoundException() {
        return ResponseEntity.notFound().build();
    }
}
