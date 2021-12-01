package nextstep.subway.line.exception;

import nextstep.subway.common.ServiceException;
import org.springframework.http.HttpStatus;

public class LineNotFoundException extends ServiceException {
  private static final String MESSAGE = "해당 지하철 노선은 존재하지 않습니다.";

  public LineNotFoundException() {
    super(HttpStatus.NOT_FOUND, MESSAGE);
  }
}
