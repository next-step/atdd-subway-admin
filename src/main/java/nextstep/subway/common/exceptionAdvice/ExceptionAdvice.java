package nextstep.subway.common.exceptionAdvice;

import nextstep.subway.common.exceptionAdvice.dto.ErrorResponse;
import nextstep.subway.common.exceptionAdvice.exception.LineNotFoundException;
import nextstep.subway.common.exceptionAdvice.exception.StationNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({LineNotFoundException.class, StationNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgsException(Exception e) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(6000, e.getMessage()));
    }
}
