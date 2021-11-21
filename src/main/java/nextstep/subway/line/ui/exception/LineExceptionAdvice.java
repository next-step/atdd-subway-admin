package nextstep.subway.line.ui.exception;

import nextstep.subway.line.ui.LineController;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = LineController.class)
public class LineExceptionAdvice {

    private static final String LINE_ALREADY_REGISTERED = "이미 등록되어 있는 노선입니다.";
    private static final String LINE_NOT_EXISTS = "존재하지 않는 노선입니다.";

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity handleDataIntegrityViolationException(
        DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(LINE_ALREADY_REGISTERED);
    }

    @ExceptionHandler(LineNotFoundException.class)
    protected ResponseEntity handleLineNotFoundException(LineNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    protected ResponseEntity handleEmptyResultDataAccessException(
        EmptyResultDataAccessException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(LINE_NOT_EXISTS);
    }
}
