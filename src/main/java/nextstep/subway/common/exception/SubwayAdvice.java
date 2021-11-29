package nextstep.subway.common.exception;

import static org.springframework.http.HttpStatus.*;

import javax.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import nextstep.subway.common.ErrorResponse;
import nextstep.subway.common.exception.ServiceException;

@ControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler(value = {EntityNotFoundException.class, DataIntegrityViolationException.class, NoResultDataException.class})
    public ResponseEntity<ErrorResponse> EntityNotFoundException(Exception e) {
        return new ResponseEntity<>(ErrorResponse.of(BAD_REQUEST, e.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(value = {ServiceException.class})
    public ResponseEntity<ErrorResponse> serviceException(Exception e) {
        return new ResponseEntity<>(ErrorResponse.of(BAD_REQUEST, e.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class, ArithmeticException.class})
    public ResponseEntity<ErrorResponse> standardException(Exception e) {
        return new ResponseEntity<>(ErrorResponse.of(BAD_REQUEST, e.getMessage()), BAD_REQUEST);
    }
}
