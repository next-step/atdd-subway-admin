package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException() {
        super("지하철역을 찾을 수 없습니다");
    }
}
