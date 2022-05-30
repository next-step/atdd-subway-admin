package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(SubwayCustomException.class)
    public ResponseEntity handleException(SubwayCustomException e) {
        return new ResponseEntity<>(e.getMessage() , e.getStatus());
    }
}
