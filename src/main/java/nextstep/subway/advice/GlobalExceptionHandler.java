package nextstep.subway.advice;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = {DataIntegrityViolationException.class})
  public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
    return ResponseEntity.badRequest().build();
  }

  @ExceptionHandler(value = {EntityNotFoundException.class})
  public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
    return ResponseEntity.notFound().build();
  }
}
