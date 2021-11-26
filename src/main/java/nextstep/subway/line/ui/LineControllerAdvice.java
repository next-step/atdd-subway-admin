package nextstep.subway.line.ui;

import nextstep.subway.common.ErrorResponse;
import nextstep.subway.exceptions.DuplicateLineException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice(basePackages = "nextstep.subway.line.ui")
public class LineControllerAdvice {
  @ExceptionHandler(DuplicateLineException.class)
  protected ResponseEntity<ErrorResponse> DuplicateLineExceptionHandler(DuplicateLineException exception) {
    return ResponseEntity
      .status(HttpStatus.CONFLICT)
      .body(ErrorResponse.of(HttpStatus.CONFLICT, exception.getMessage()));
  }

  @ExceptionHandler(NoSuchElementException.class)
  protected ResponseEntity<ErrorResponse> DuplicateLineExceptionHandler(NoSuchElementException exception) {
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(ErrorResponse.of(HttpStatus.NOT_FOUND, exception.getMessage()));
  }
}
