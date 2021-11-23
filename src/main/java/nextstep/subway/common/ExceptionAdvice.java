package nextstep.subway.common;

import nextstep.subway.line.application.exception.LineNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity handleDataIntegrityViolationException(
        DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(LineNotFoundException.class)
    protected ResponseEntity handleLineNotFoundException(LineNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    protected ResponseEntity handleEmptyResultDataAccessException(
        EmptyResultDataAccessException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
