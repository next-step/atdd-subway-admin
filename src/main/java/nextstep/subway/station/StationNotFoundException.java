package nextstep.subway.station;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "station not found")
public class StationNotFoundException extends RuntimeException {
}
