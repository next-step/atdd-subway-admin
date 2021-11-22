package nextstep.subway.common;

import javax.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
//https://bcp0109.tistory.com/303
@ControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> EntityNotFoundException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(9999);
        if (e.getMessage() != null) {
            errorResponse.setMessage(e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> dataIntegrityViolationException(Exception e) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(9999);
        if (e.getMessage() != null) {
            errorResponse.setMessage(e.getMessage());
        }

        return ResponseEntity.badRequest().body(errorResponse);
    }
}
