package nextstep.subway.common.exception;

import nextstep.subway.line.application.exception.InvalidDistanceException;
import nextstep.subway.line.application.exception.InvalidSectionException;
import nextstep.subway.line.application.exception.LineNotFoundException;
import nextstep.subway.line.application.exception.SectionNotFoundException;
import nextstep.subway.station.application.exception.StationNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ApiError> handle(DataIntegrityViolationException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST, ex.getLocalizedMessage(), ex.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ApiError> handle(InvalidDataAccessApiUsageException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST, ex.getLocalizedMessage(), ex.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ApiError> handle(EmptyResultDataAccessException ex) {
        ApiError apiError = new ApiError(NOT_FOUND, ex.getLocalizedMessage(), ex.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ApiError> handle(StationNotFoundException ex) {
        ApiError apiError = new ApiError(NOT_FOUND, ex.getLocalizedMessage(), ex.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ApiError> handle(LineNotFoundException ex) {
        ApiError apiError = new ApiError(NOT_FOUND, ex.getLocalizedMessage(), ex.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ApiError> handle(SectionNotFoundException ex) {
        ApiError apiError = new ApiError(NOT_FOUND, ex.getLocalizedMessage(), ex.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ApiError> handle(InvalidSectionException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST, ex.getLocalizedMessage(), ex.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ApiError> handle(InvalidDistanceException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST, ex.getLocalizedMessage(), ex.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handle(RuntimeException ex) {
        ApiError apiError = new ApiError(INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), ex.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }
}
