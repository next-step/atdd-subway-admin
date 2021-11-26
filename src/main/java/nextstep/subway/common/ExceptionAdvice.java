package nextstep.subway.common;

import java.util.NoSuchElementException;
import nextstep.subway.common.exception.IllegalDistanceException;
import nextstep.subway.common.exception.LineNotFoundException;
import nextstep.subway.common.exception.StationNotFoundException;
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

    @ExceptionHandler(value = {LineNotFoundException.class, StationNotFoundException.class})
    protected ResponseEntity handleNotFoundException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    protected ResponseEntity handleEmptyResultDataAccessException(
        EmptyResultDataAccessException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(IllegalDistanceException.class)
    protected ResponseEntity handleIllegalDistanceException(IllegalDistanceException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
