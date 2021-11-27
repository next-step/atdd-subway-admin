package nextstep.subway.line.advice;

import nextstep.subway.line.ui.LineController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = LineController.class)
public class LineControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgsException(IllegalArgumentException e) {
        return ResponseEntity.noContent().build();
    }
}
