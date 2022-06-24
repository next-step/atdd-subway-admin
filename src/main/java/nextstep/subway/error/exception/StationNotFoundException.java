package nextstep.subway.error.exception;

import nextstep.subway.error.SubwayError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class StationNotFoundException extends SubWayApiException {

    public StationNotFoundException(Long stationId) {
        super(SubwayError.STATION_NOT_FOUND, String.format("stationId(%d)을 가진 Station 찾을 수 없습니다.", stationId));
    }
}
