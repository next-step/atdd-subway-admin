package nextstep.subway.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CanNotConnectSectionException extends RuntimeException {

    public CanNotConnectSectionException() {
        super("구간을 연결할 수 없습니다.");
    }
}
