package nextstep.subway.line.exception;

import nextstep.subway.common.ServiceException;
import org.springframework.http.HttpStatus;

public class LineDuplicateException extends ServiceException {

  private static final String MESSAGE = "이미 등록된 노선이 존재합니다.";

  public LineDuplicateException(String duplicateLineName) {
    super(HttpStatus.BAD_REQUEST, MESSAGE + ": " + duplicateLineName);
  }
}
