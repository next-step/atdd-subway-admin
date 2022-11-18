package nextstep.subway.common;

import nextstep.subway.exception.CannotConnectSectionException;
import nextstep.subway.exception.CannotDeleteSectionException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.UpdateExistingSectionException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class SubWayExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({ DataIntegrityViolationException.class, IllegalArgumentException.class,
        NotFoundException.class, CannotConnectSectionException.class, UpdateExistingSectionException.class,
        CannotDeleteSectionException.class })
    protected ResponseEntity<Void> handle() {
        return ResponseEntity.badRequest().build();
    }
}
