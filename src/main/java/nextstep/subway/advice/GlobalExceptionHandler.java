package nextstep.subway.advice;

import nextstep.subway.error.CanNotRemovableSectionException;
import nextstep.subway.error.LineNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(LineNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(CanNotRemovableSectionException.class)
    public ResponseEntity<ApiError> handleCanNotRemovableSectionException(CanNotRemovableSectionException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

}
