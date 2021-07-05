package nextstep.subway.common;

import nextstep.subway.exception.CanNotAddNewSectionException;
import nextstep.subway.exception.CanNotFoundLineException;
import nextstep.subway.exception.CanNotFoundStationException;
import nextstep.subway.exception.CanNotRemoveStationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(CanNotAddNewSectionException.class)
    public ResponseEntity handleCanNotAddNewSectionException(CanNotAddNewSectionException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(CanNotFoundLineException.class)
    public ResponseEntity handleCanNotFoundLineException(CanNotFoundLineException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(CanNotFoundStationException.class)
    public ResponseEntity handleCanNotFoundStationException(CanNotFoundStationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(CanNotRemoveStationException.class)
    public ResponseEntity handleCanNotRemoveStationException(CanNotRemoveStationException e) {
        return ResponseEntity.badRequest().build();
    }
}
