package nextstep.subway.error.exception;

import nextstep.subway.error.SubwayError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class LineNotFoundException extends SubWayApiException {

    public LineNotFoundException(Long lineId) {
        super(SubwayError.LINE_NOT_FOUND, String.format("LineId(%d)을 가진 Line을 찾을 수 없습니다.", lineId));
    }
}
