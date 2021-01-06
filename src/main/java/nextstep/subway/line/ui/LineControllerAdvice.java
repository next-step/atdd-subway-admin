package nextstep.subway.line.ui;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.line.exception.SectionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class LineControllerAdvice {

  @ExceptionHandler({SectionException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public Object badRequest(HttpServletRequest request) {
    log.error("bad request error");
    log.error(String.valueOf(request.getMethod()));
    log.error(String.valueOf(request.getRequestURL()));
    return ResponseEntity.badRequest().build();
  }

}
