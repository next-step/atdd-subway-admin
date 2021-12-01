package nextstep.subway.station.exception;

import nextstep.subway.common.ServiceException;
import org.springframework.http.HttpStatus;

public class StationDuplicateException extends ServiceException {
  private static final String MESSAGE = "지하철 역 목록에 중복된 지하철 역이 있습니다.";

  public StationDuplicateException(String stationName) {
    super(HttpStatus.BAD_REQUEST, MESSAGE + " 입력: " + stationName);
  }
}
