package nextstep.subway.line.ui.exception;

import nextstep.subway.line.ui.exception.LineNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LineExceptionAdvice {

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity handleDataIntegrityViolationException(
        DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 등록되어 있는 노선입니다.");
    }

    @ExceptionHandler(LineNotFoundException.class)
    protected ResponseEntity handleLineNotFoundException(LineNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
