package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException() {
        super("노선을 찾을 수 없습니다");
    }
}
