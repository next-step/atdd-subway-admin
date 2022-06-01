package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidDistanceException extends RuntimeException {
    public InvalidDistanceException() {
        super("구간 길이에 오류가 발생했습니다.");
    }
}
