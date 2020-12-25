package nextstep.subway.common;

import nextstep.subway.common.exception.BadRequestException;
import nextstep.subway.common.exception.NotExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler({DataIntegrityViolationException.class, NotExistsException.class, BadRequestException.class})
    public ResponseEntity<?> handleIllegalArgsException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
