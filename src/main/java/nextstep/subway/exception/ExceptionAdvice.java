package nextstep.subway.exception;


import nextstep.subway.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> exceptionHandler(RuntimeException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(String.format("%s message: %s ", exception, exception.getMessage())));
    }

}
