package nextstep.subway.line.ui;

import nextstep.subway.common.ErrorResponse;
import nextstep.subway.common.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice(basePackageClasses = LineController.class)
public class LineControllerAdvice {
  @ExceptionHandler(ServiceException.class)
  protected ResponseEntity<ErrorResponse> serviceExceptionHandler(ServiceException exception) {
    return ResponseEntity
      .status(exception.status())
      .body(ErrorResponse.of(exception.getMessage()));
  }
}
