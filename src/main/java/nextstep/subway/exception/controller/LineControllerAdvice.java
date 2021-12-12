package nextstep.subway.exception.controller;

import nextstep.subway.exception.DuplicateLineNameException;
import nextstep.subway.exception.NotFoundSectionException;
import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.dto.ErrorResponse;
import nextstep.subway.exception.NotFoundLineException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LineControllerAdvice {

    @ExceptionHandler(DuplicateLineNameException.class)
    public ResponseEntity<ErrorResponse> duplicateLineNameException(DuplicateLineNameException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(NotFoundLineException.class)
    public ResponseEntity<ErrorResponse> notFoundLineException(NotFoundLineException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(NotFoundStationException.class)
    public ResponseEntity<ErrorResponse> notFoundStationException(NotFoundStationException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(NotFoundSectionException.class)
    public ResponseEntity<ErrorResponse> notFoundSectionException(NotFoundSectionException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> illegalException(IllegalStateException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
}

