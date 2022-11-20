package nextstep.subway.exception;

import nextstep.subway.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GeneralExceptionHandler {

    private ResponseEntity<ErrorResponse> newResponse(Throwable throwable, HttpStatus status) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("BAD_REQUEST", status.value(), throwable.getMessage()));
    }

    @ExceptionHandler({InvalidParameterException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception e) {
        return newResponse(e, HttpStatus.BAD_REQUEST);
    }

}
