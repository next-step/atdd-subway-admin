package nextstep.subway.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.exception.dto.ErrorResponse;
import nextstep.subway.exception.section.InvalidDistanceException;
import nextstep.subway.exception.section.NotFoundSectionException;

@RestControllerAdvice
public class SectionControllerAdvice extends ControllerAdvice {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> noValidatedInputException(DataIntegrityViolationException e) {
        return getBadResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidDistanceException.class)
    public ResponseEntity<ErrorResponse> invalidDistanceException(InvalidDistanceException e) {
        return getBadResponse(e.getMessage());
    }

    @ExceptionHandler(NotFoundSectionException.class)
    public ResponseEntity<ErrorResponse> notFoundSectionException(NotFoundSectionException e) {
        return getBadResponse(e.getMessage());
    }

}
