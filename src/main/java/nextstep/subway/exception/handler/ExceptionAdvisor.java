package nextstep.subway.exception.handler;

import nextstep.subway.exception.DuplicateDataException;
import nextstep.subway.exception.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvisor {
    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ErrorDto> handleDuplicateDataException(DuplicateDataException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.createErrorDto());
    }
}
