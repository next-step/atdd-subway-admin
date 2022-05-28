package nextstep.subway.exception;

import nextstep.subway.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNoSuchElementFoundException(NoSuchElementFoundException error) {
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(), error.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
