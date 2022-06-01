package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidSectionException extends RuntimeException {
    public InvalidSectionException() {
        super("추가할 수 없는 구간입니다.");
    }
}
