package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class StationNotFoundException extends Exception {

    public StationNotFoundException(Long stationId) {
        super(String.format("stationId(%d)을 가진 Station 찾을 수 없습니다.", stationId));
    }
}
