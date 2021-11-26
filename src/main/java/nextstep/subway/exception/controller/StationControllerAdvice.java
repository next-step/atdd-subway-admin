package nextstep.subway.exception.controller;

import nextstep.subway.exception.DuplicateLineNameException;
import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.exception.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StationControllerAdvice {

    @ExceptionHandler(DuplicateLineNameException.class)
    public ResponseEntity<ErrorResponse> duplicateLineNameException(DuplicateLineNameException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(NotFoundLineException.class)
    public ResponseEntity<ErrorResponse> notFoundLineException(NotFoundLineException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
}
