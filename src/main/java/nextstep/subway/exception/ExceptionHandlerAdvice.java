package nextstep.subway.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(NotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(SectionCreateFailException.class)
    public ResponseEntity handleSectionCreateFailException(SectionCreateFailException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(SectionDeleteFailException.class)
    public ResponseEntity handleSectionDeleteFailException(SectionDeleteFailException e) {
        return ResponseEntity.badRequest().build();
    }
}
