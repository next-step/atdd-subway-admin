package nextstep.subway.exception.handler;

import nextstep.subway.exception.DuplicateDataException;
import nextstep.subway.exception.ErrorDto;
import nextstep.subway.exception.NoSuchDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvisor {
    private static final Logger log = LoggerFactory.getLogger(ExceptionAdvisor.class);

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ErrorDto> handleDuplicateDataException(DuplicateDataException exception) {
        log.error("DuplicateDataException", exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.createErrorDto());
    }

    @ExceptionHandler(NoSuchDataException.class)
    public ResponseEntity<ErrorDto> handleNoSuchDataException(NoSuchDataException exception) {
        log.error("NoSuchDataException", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.createErrorDto());
    }
}
