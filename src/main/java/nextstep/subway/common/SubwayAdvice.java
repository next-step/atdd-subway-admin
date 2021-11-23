package nextstep.subway.common;

import static org.springframework.http.HttpStatus.*;

import javax.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler(value = {EntityNotFoundException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> EntityNotFoundException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(ErrorCode.DB_ERROR, e.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(value = {ServiceException.class})
    public ResponseEntity<ErrorResponse> serviceException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(ErrorCode.SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
    }
}
