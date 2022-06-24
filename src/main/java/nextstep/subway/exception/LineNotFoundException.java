package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class LineNotFoundException extends Exception {

    public LineNotFoundException(Long lineId) {
        super(String.format("LineId(%d)을 가진 Line을 찾을 수 없습니다.", lineId));
    }
}
