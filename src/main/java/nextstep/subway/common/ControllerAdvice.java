package nextstep.subway.common;

import nextstep.subway.exception.CanNotAddNewSectionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(CanNotAddNewSectionException.class)
    public ResponseEntity handleCanNotAddNewSectionException(CanNotAddNewSectionException e) {
        return ResponseEntity.badRequest().build();
    }
}
