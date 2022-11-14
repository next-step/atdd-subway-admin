package nextstep.subway.ui;

import nextstep.subway.dto.ErrorResponse;
import nextstep.subway.exception.CannotAddSectionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(CannotAddSectionException.class)
    public ResponseEntity<ErrorResponse> cannotAddSectionException(CannotAddSectionException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception));
    }
}
