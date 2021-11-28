package nextstep.subway.station.exception;

import nextstep.subway.common.ServiceException;
import org.springframework.http.HttpStatus;

public class StationNotFoundException extends ServiceException {

  private static final String MESSAGE = "해당 지하철 역을 찾을 수 없습니다.";

  public StationNotFoundException() {
    super(HttpStatus.NOT_FOUND, MESSAGE);
  }
}
