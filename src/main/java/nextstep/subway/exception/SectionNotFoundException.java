package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SectionNotFoundException extends RuntimeException {
    public SectionNotFoundException() {
        super("노선 내 구간을 찾을 수 없습니다");
    }
    public SectionNotFoundException(String message) {
        super(message);
    }
}
