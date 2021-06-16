package nextstep.subway.common.exceptionAdvice;

import nextstep.subway.common.exceptionAdvice.dto.ErrorResponse;
import nextstep.subway.common.exceptionAdvice.exception.LineNotFoundException;
import nextstep.subway.common.exceptionAdvice.exception.RemoveSectionException;
import nextstep.subway.common.exceptionAdvice.exception.StationNotFoundException;
import nextstep.subway.common.exceptionAdvice.exception.StationOfLineNotFoundException;
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

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgsException(LineNotFoundException e) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(ErrorCode.LINE_NOT_FOUND_EXCEPTION.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgsException(StationNotFoundException e) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(ErrorCode.STATION_NOT_FOUND_EXCEPTION.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(RemoveSectionException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgsException(RemoveSectionException e) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(ErrorCode.REMOVE_SECTION_EXCEPTION.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(StationOfLineNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgsException(StationOfLineNotFoundException e) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(ErrorCode.STATION_OF_LINE_NOT_FOUND_EXCEPTION.getErrorCode(), e.getMessage()));
    }
}
