package nextstep.subway.line.ui;

import nextstep.subway.common.ErrorResponse;
import nextstep.subway.exceptions.DuplicateLineException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "nextstep.subway.line.ui")
public class LineControllerAdvice {
  @ExceptionHandler(DuplicateLineException.class)
  protected ResponseEntity<ErrorResponse> DuplicateLineExceptionHandler(DuplicateLineException exception) {
    return ResponseEntity
      .status(HttpStatus.CONFLICT)
      .body(ErrorResponse.of(HttpStatus.CONFLICT, exception.getMessage()));
  }
}
